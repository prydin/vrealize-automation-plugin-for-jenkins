/*
 * Copyright (c) 2020 VMware, Inc
 *
 *  SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vmware.vra.jenkinsplugin.vra;

import static com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest.StatusEnum.ABORTED;
import static com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest.StatusEnum.FAILED;
import static com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest.StatusEnum.SUCCESSFUL;
import static com.vmware.vra.jenkinsplugin.util.MapUtils.mapOf;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItem;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemRequest;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemRequestResponse;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemVersion;
import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfCatalogItem;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfCatalogItemVersion;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfDeployment;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfResource;
import com.vmware.vra.jenkinsplugin.model.catalog.Resource;
import com.vmware.vra.jenkinsplugin.model.catalog.ResourceAction;
import com.vmware.vra.jenkinsplugin.model.catalog.ResourceActionRequest;
import com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest;
import com.vmware.vra.jenkinsplugin.model.iaas.Project;
import com.vmware.vra.jenkinsplugin.model.iaas.ProjectResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VraApi implements Serializable {
  private static final long serialVersionUID = -3538449737600216823L;
  private static final long deploymentPollInterval = 30000;
  private static final LoadingCache<String, Pattern> patterns =
      CacheBuilder.newBuilder()
          .maximumSize(1000)
          .expireAfterWrite(10, TimeUnit.MINUTES)
          .build(
              new CacheLoader<String, Pattern>() {
                @Override
                public Pattern load(final String pattern) {
                  return Pattern.compile(pattern);
                }
              });
  private final VraClient vraClient;

  public VraApi(final String url, final String token, final boolean trustSelfSignedCert)
      throws VRAException {
    this(new VraClient(url, token, trustSelfSignedCert));
  }

  public VraApi(
      final String url,
      final String domain,
      final String username,
      final String password,
      final boolean trustSelfSignedCert)
      throws VRAException {
    this(new VraClient(url, domain, username, password, trustSelfSignedCert));
  }

  public VraApi(final VraClient vraClient) {
    this.vraClient = vraClient;
  }

  public static Pattern getResourcePattern(final String resourceName) {
    try {
      return patterns.get(resourceName + "(\\[[0-9]+\\])?");
    } catch (final Exception e) {
      throw new IllegalArgumentException(
          "Something went wrong while parsing regexp for resource " + resourceName, e);
    }
  }

  private static void checkResponseSingleton(final List<?> content) throws VRAException {
    if (content == null) {
      throw new VRAException("No content was found");
    }
    if (content.size() != 1) {
      throw new VRAException("Expected 1 item, got " + content.size());
    }
  }

  private static Object getProperty(final Object props, final String key) {
    if (!(props instanceof Map)) {
      throw new IllegalArgumentException(
          "Expected properties to be a Map but got" + props.getClass().getName());
    }
    return ((Map<String, Object>) props).get("address");
  }

  public CatalogItem getCatalogItemByName(final String name) throws VRAException {
    final PageOfCatalogItem page =
        vraClient.get(
            "/catalog/api/items",
            mapOf("search", name, "size", "1000000"),
            PageOfCatalogItem.class);
    final List<CatalogItem> content = page.getContent();
    return content.stream().filter((c) -> c.getName().equals(name)).findFirst().orElse(null);
  }

  public CatalogItemVersion getLatestCatalogItemVersion(final String id) throws VRAException {
    final PageOfCatalogItemVersion page =
        vraClient.get(
            "/catalog/api/items/" + id + "/versions",
            mapOf("size", "1"),
            PageOfCatalogItemVersion.class);
    checkResponseSingleton(page.getContent());
    return page.getContent().size() > 0 ? page.getContent().get(0) : null;
  }

  public Project getProjectByName(final String name) throws VRAException {
    final ProjectResult projs =
        vraClient.get(
            "/iaas/api/projects", mapOf("$filter", "name eq '" + name + "'"), ProjectResult.class);
    checkResponseSingleton(projs.getContent());
    return projs.getContent().get(0);
  }

  public Project getProjectById(final String id) throws VRAException {
    return vraClient.get("/iaas/api/projects/" + id, null, Project.class);
  }

  public CatalogItem getCatalogItemById(final String id) throws VRAException {
    return vraClient.get("/catalog/api/items/" + id, null, CatalogItem.class);
  }

  public List<String> waitForIPAddresses(
      final String deploymentId, final String resourceName, final long timeout)
      throws VRAException, InterruptedException, TimeoutException {
    final Pattern resourcePattern = getResourcePattern(resourceName);
    return pollWithTimeout(
        timeout,
        () -> {
          final Deployment dep = getCatalogDeploymentById(deploymentId, true);
          if (dep == null) {
            throw new IllegalArgumentException("Deployment doesn't exist: " + deploymentId);
          }
          final List<String> result = new ArrayList<>(dep.getResources().size());
          boolean missingAddress = false;
          boolean missingResource = true;
          for (final Resource r : dep.getResources()) {
            if (!resourcePattern.matcher(r.getName()).matches()) {
              continue;
            }
            missingResource = false;
            final String addr = (String) getProperty(r.getProperties(), "address");
            if (addr != null) {
              result.add(addr);
            } else {
              missingAddress = true;
            }
          }
          if (missingResource) {
            throw new IllegalArgumentException("Resource " + resourceName + " doesn't exist");
          }
          return missingAddress ? Optional.empty() : Optional.of(result);
        });
  }

  public CatalogItemRequestResponse[] deployFromCatalog(
      final String ciName,
      final String version,
      final String project,
      final String deploymentName,
      final String reason,
      final Map<String, Object> inputs,
      final int count)
      throws VRAException {
    final CatalogItem ci = getCatalogItemByName(ciName);
    if (ci == null) {
      throw new IllegalArgumentException("Catalog item named " + ciName + " not found");
    }
    final Project proj = getProjectByName(project);

    final CatalogItemRequest cir = new CatalogItemRequest();
    cir.setBulkRequestCount(count);
    cir.setDeploymentName(deploymentName);
    cir.setProjectId(proj.getId());
    cir.setVersion( // Get latest version if not specified
        version != null ? version : getLatestCatalogItemVersion(ci.getId().toString()).getId());
    cir.setReason(reason);
    cir.setInputs(inputs != null ? inputs : Collections.EMPTY_MAP);
    return vraClient.post(
        "/catalog/api/items/" + ci.getId() + "/request",
        null,
        cir,
        CatalogItemRequestResponse[].class);
  }

  public Deployment getCatalogDeploymentById(
      final String deploymentId, final boolean expandResources) throws VRAException {
    return vraClient.get(
        "/deployment/api/deployments/" + deploymentId,
        mapOf("expandResources", Boolean.toString(expandResources)),
        Deployment.class);
  }

  public Deployment getCatalogDeploymentByName(
      final String deploymentName, final boolean expandResources) throws VRAException {
    final PageOfDeployment deps =
        vraClient.get(
            "/deployment/api/deployments",
            mapOf("expandResources", Boolean.toString(expandResources), "name", deploymentName),
            PageOfDeployment.class);
    checkResponseSingleton(deps.getContent());
    return deps.getContent().get(0);
  }

  public DeploymentRequest deleteCatalogDeployment(final String deploymentId) throws VRAException {
    return vraClient.delete(
        "/deployment/api/deployments/" + deploymentId, null, DeploymentRequest.class);
  }

  public Deployment waitForCatalogDeployment(final String deploymentId, final long timeout)
      throws TimeoutException, VRAException, InterruptedException {
    return pollWithTimeout(
        timeout,
        () -> {
          final Deployment dep = getCatalogDeploymentById(deploymentId, false);
          if (dep != null && dep.getStatus() != null) {
            if (!dep.getStatus().getValue().endsWith("_INPROGRESS")) {
              return Optional.of(getCatalogDeploymentById(deploymentId, true));
            }
          }
          return Optional.empty();
        });
  }

  private <T> T pollWithTimeout(final long timeout, final Callable<Optional<T>> job)
      throws TimeoutException, VRAException, InterruptedException {
    final long start = System.currentTimeMillis();
    for (; ; ) {
      final long remaining = timeout - (System.currentTimeMillis() - start);
      try {
        final Optional<T> result = job.call();
        if (result.isPresent()) {
          return result.get();
        }
      } catch (final VRAException e) {
        throw e;
      } catch (final Exception e) {
        throw new RuntimeException("Unexpected exception", e);
      }
      if (remaining <= 0) {
        throw new TimeoutException("Timeout while waiting for deployment to finish");
      }
      Thread.sleep(Math.min(remaining, deploymentPollInterval));
    }
  }

  public DeploymentRequest getDeploymentRequest(final String id) throws VRAException {
    return vraClient.get("/deployment/api/requests/" + id, null, DeploymentRequest.class);
  }

  public DeploymentRequest waitForRequestCompletion(final String id, final long timeout)
      throws VRAException, TimeoutException, InterruptedException {
    return pollWithTimeout(
        timeout,
        () -> {
          final DeploymentRequest dr = getDeploymentRequest(id);
          if (dr.getStatus() == SUCCESSFUL
              || dr.getStatus() == ABORTED
              || dr.getStatus() == FAILED) {
            return Optional.of(dr);
          }
          return Optional.empty();
        });
  }

  public List<DeploymentRequest> waitForRequestCompletion(
      final List<String> ids, final long timeout)
      throws VRAException, TimeoutException, InterruptedException {
    final List<DeploymentRequest> result = new ArrayList<>(ids.size());
    final long start = System.currentTimeMillis();
    for (final String id : ids) {
      final long remaining = timeout - (System.currentTimeMillis() - start);
      if (remaining <= 0) {
        throw new TimeoutException("Timeout while waiting for multiple deployments to finish");
      }
      result.add(waitForRequestCompletion(id, Math.min(remaining, timeout)));
    }
    return result;
  }

  public DeploymentRequest submitDeploymentAction(
      final String deploymentId,
      final String actionId,
      final String reason,
      final Map<String, Object> inputs)
      throws VRAException {
    final ResourceActionRequest payload = new ResourceActionRequest();
    payload.setActionId(actionId);
    payload.setInputs(inputs != null ? inputs : Collections.EMPTY_MAP);
    payload.setReason(reason);
    return vraClient.post(
        "/deployment/api/deployments/" + deploymentId + "/requests",
        null,
        payload,
        DeploymentRequest.class);
  }

  public DeploymentRequest submitResourceAction(
      final String deploymentId,
      final String resourceId,
      final String actionId,
      final String reason,
      final Map<String, Object> inputs)
      throws VRAException {
    final ResourceActionRequest payload = new ResourceActionRequest();
    payload.setActionId(actionId);
    payload.setInputs(inputs != null ? inputs : Collections.EMPTY_MAP);
    payload.setReason(reason);
    return vraClient.post(
        "/deployment/api/deployments/" + deploymentId + "/resources/" + resourceId + "/requests",
        null,
        payload,
        DeploymentRequest.class);
  }

  public List<Resource> getResourcesForDeployment(final String deploymentId) throws VRAException {
    final PageOfResource response =
        vraClient.get(
            "/deployment/api/deployments/" + deploymentId + "/resources",
            mapOf("size", "10000"),
            PageOfResource.class);
    assert (response != null);
    assert (response.getContent() != null);
    return response.getContent();
  }

  public List<Resource> getNamedResourcesForDeployment(
      final String deploymentId, final String resourceName) throws VRAException {
    return getResourcesForDeployment(deploymentId).stream()
        .filter((r) -> r.getName().equals(resourceName))
        .collect(Collectors.toList());
  }

  public ResourceAction getResourceActionDetails(
      final String deploymentId, final String resourceId, final String actionName)
      throws VRAException {
    return vraClient.get(
        "/deployment/api/deployments/"
            + deploymentId
            + "/resources/"
            + resourceId
            + "/actions/"
            + actionName,
        Collections.EMPTY_MAP,
        ResourceAction.class);
  }

  public ResourceAction[] getResourceActions(final String deploymentId, final String resourceId)
      throws VRAException {
    return vraClient.get(
        "/deployment/api/deployments/" + deploymentId + "/resources/" + resourceId + "/actions",
        Collections.EMPTY_MAP,
        ResourceAction[].class);
  }
}
