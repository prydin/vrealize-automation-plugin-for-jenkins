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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItem;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemRequest;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemRequestResponse;
import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemVersion;
import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfCatalogItem;
import com.vmware.vra.jenkinsplugin.model.catalog.PageOfCatalogItemVersion;
import com.vmware.vra.jenkinsplugin.model.catalog.ResourceAction;
import com.vmware.vra.jenkinsplugin.model.catalog.ResourceActionRequest;
import com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest;
import com.vmware.vra.jenkinsplugin.model.iaas.Project;
import com.vmware.vra.jenkinsplugin.model.iaas.ProjectResult;
import com.vmware.vra.jenkinsplugin.testutils.FileUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import org.junit.Test;

public class VraApiTest {
  private static final String catalogItemName = "jenkins-test";
  private static final String projectName = "JenkinsTest";
  private static final String catalogItemId = "563f6b86-e379-3965-81eb-90471da4d688";
  private static final String projectId = "9de81991-4063-43b8-9542-dbaff1e588f8";
  private static final String deploymentId = "17a6f622-2022-4482-bfb7-6fa889dabaa5";
  private static final String version = "2";
  private static final String resourceName = "UbuntuMachine";
  private static final String actionName = "Cloud.vSphere.Machine.Snapshot.Revert";
  private static final String shortActionName = "Snapshot.Revert";
  private static final Pattern ipPattern =
      Pattern.compile(
          "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

  @Test
  public void testLogin() throws VRAException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    assertNotNull(client);
  }

  @Test
  public void testLoginUsername() throws VRAException {
    // Only works on-premises, so skip if we only have access to cloud.
    final String url = System.getenv("VRA_ONPREM_URL");
    final String username = System.getenv("VRA_USERNAME");
    if (url == null || username == null) {
      System.err.println("VRA_ONPREM_URL or VRA_USERNAME not set. Skipping test");
      return;
    }
    final VraApi client =
        new VraApi(url, System.getenv("VRA_DOMAIN"), username, System.getenv("VRA_PASSWORD"), true);
    assertNotNull(client);
  }

  @Test
  public void testGetCatalogItemByName() throws VRAException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    final CatalogItem ci = client.getCatalogItemByName(catalogItemName);
    assertNotNull(ci);
    assertEquals(catalogItemName, ci.getName());
  }

  @Test
  public void testGetCatalogItemByNameMocked() throws Exception {
    final Gson gson = new Gson();
    final PageOfCatalogItem wanted =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/PageOfCatalogItem.json"), PageOfCatalogItem.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/catalog/api/items"), any(), eq(PageOfCatalogItem.class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final CatalogItem ci = client.getCatalogItemByName(catalogItemName);
    assertNotNull(ci);
    assertEquals(catalogItemName, ci.getName());
    verify(mocked, times(1)).get(eq("/catalog/api/items"), any(), eq(PageOfCatalogItem.class));
  }

  @Test
  public void testGetCatalogItemById() throws VRAException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    CatalogItem ci = client.getCatalogItemByName(catalogItemName);
    assertNotNull(ci);
    assertEquals(catalogItemName, ci.getName());
    ci = client.getCatalogItemById(ci.getId().toString());
    assertNotNull(ci);
    assertEquals(catalogItemName, ci.getName());
  }

  @Test
  public void testGetCatalogItemByIdMocked() throws Exception {
    final Gson gson = new Gson();
    final CatalogItem wanted =
        gson.fromJson(FileUtils.loadResource("/apiresults/CatalogItem.json"), CatalogItem.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/catalog/api/items/" + catalogItemId), any(), eq(CatalogItem.class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final CatalogItem ci = client.getCatalogItemById(catalogItemId);
    assertNotNull(ci);
    assertEquals(catalogItemName, ci.getName());
    verify(mocked, times(1))
        .get(eq("/catalog/api/items/" + catalogItemId), any(), eq(CatalogItem.class));
  }

  @Test
  public void testGetResourceActionDetailsMocked() throws Exception {
    final Gson gson = new Gson();
    final ResourceAction wanted =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/ResourceAction.json"), ResourceAction.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(
            eq(
                "/deployment/api/deployments/"
                    + deploymentId
                    + "/resources/"
                    + resourceName
                    + "/actions/"
                    + actionName),
            any(),
            eq(ResourceAction.class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final ResourceAction ra =
        client.getResourceActionDetails(deploymentId, resourceName, actionName);
    verify(mocked, times(1))
        .get(
            eq(
                "/deployment/api/deployments/"
                    + deploymentId
                    + "/resources/"
                    + resourceName
                    + "/actions/"
                    + actionName),
            any(),
            eq(ResourceAction.class));
    assertNotNull(ra);
    assertEquals(shortActionName, ra.getName());
  }

  @Test
  public void testGetResourceActionsMocked() throws Exception {
    final Gson gson = new Gson();
    final ResourceAction[] wanted =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/ResourceActionList.json"), ResourceAction[].class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(
            eq(
                "/deployment/api/deployments/"
                    + deploymentId
                    + "/resources/"
                    + resourceName
                    + "/actions"),
            any(),
            eq(ResourceAction[].class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final ResourceAction[] ra = client.getResourceActions(deploymentId, resourceName);
    verify(mocked, times(1))
        .get(
            eq(
                "/deployment/api/deployments/"
                    + deploymentId
                    + "/resources/"
                    + resourceName
                    + "/actions"),
            any(),
            eq(ResourceAction[].class));
    assertNotNull(ra);
    assertEquals(18, ra.length);
  }

  @Test
  public void getProjectByName() throws VRAException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    final Project proj = client.getProjectByName(projectName);
    assertNotNull(proj);
    assertEquals(projectName, proj.getName());
  }

  @Test
  public void getProjectByNameMocked() throws Exception {
    final Gson gson = new Gson();
    final ProjectResult wanted =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/ProjectResult.json"), ProjectResult.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/iaas/api/projects"), any(), eq(ProjectResult.class))).thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final Project proj = client.getProjectByName(projectName);
    assertNotNull(proj);
    assertEquals(projectName, proj.getName());
    verify(mocked, times(1)).get(eq("/iaas/api/projects"), any(), eq(ProjectResult.class));
  }

  @Test
  public void getProjectById() throws VRAException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    Project proj = client.getProjectByName(projectName);
    assertNotNull(proj);
    assertEquals(projectName, proj.getName());
    proj = client.getProjectById(proj.getId());
    assertNotNull(proj);
    assertEquals(projectName, proj.getName());
  }

  @Test
  public void getProjectByIdMocked() throws Exception {
    final Gson gson = new Gson();
    final Project wanted =
        gson.fromJson(FileUtils.loadResource("/apiresults/Project.json"), Project.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/iaas/api/projects/" + projectId), any(), eq(Project.class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final Project proj = client.getProjectById(projectId);
    assertNotNull(proj);
    assertEquals(projectName, proj.getName());
    verify(mocked, times(1)).get(eq("/iaas/api/projects/" + projectId), any(), eq(Project.class));
  }

  @Test
  public void testGetLatestVersionMocked() throws Exception {
    final Gson gson = new Gson();
    final PageOfCatalogItemVersion wanted =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/PageOfCatalogItemVersion.json"),
            PageOfCatalogItemVersion.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(
            eq("/catalog/api/items/" + catalogItemId + "/versions"),
            any(),
            eq(PageOfCatalogItemVersion.class)))
        .thenReturn(wanted);
    final VraApi client = new VraApi(mocked);
    final CatalogItemVersion v = client.getLatestCatalogItemVersion(catalogItemId);
    assertNotNull(v);
    assertEquals("10", v.getId());
  }

  @Test
  public void testGetLatestVersion() throws Exception {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    final CatalogItemVersion v = client.getLatestCatalogItemVersion(catalogItemId);
    assertNotNull(v);
    assertEquals("11", v.getId());
  }

  @Test
  public void waitForAddreesMocked() throws Exception {
    final Gson gson = new Gson();
    final Deployment deployment =
        gson.fromJson(FileUtils.loadResource("/apiresults/Deployment.json"), Deployment.class);
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/deployment/api/deployments/" + deploymentId), any(), eq(Deployment.class)))
        .thenReturn(deployment);
    final VraApi client = new VraApi(mocked);
    final List<String> addresses = client.waitForIPAddresses(deploymentId, resourceName, 300);
    assertNotNull(addresses);
    assertTrue(ipPattern.matcher(addresses.get(0)).matches());
    assertEquals(1, addresses.size());
    verify(mocked, times(1))
        .get(eq("/deployment/api/deployments/" + deploymentId), any(), eq(Deployment.class));
  }

  @Test
  public void testDeployment() throws VRAException, TimeoutException, InterruptedException {
    final String url = System.getenv("VRA_URL");
    if (url == null) {
      System.err.println("VRA_URL not set. Skipping test");
      return;
    }
    final VraApi client = new VraApi(url, System.getenv("VRA_TOKEN"), true);
    final String depName = "Test " + UUID.randomUUID().toString();
    final CatalogItemRequestResponse[] resp =
        client.deployFromCatalog(
            catalogItemName, version, projectName, depName, "Some reason", null, 1);
    assertEquals(1, resp.length);
    assertNotNull(resp[0]);
    assertNotNull(resp[0].getDeploymentId());
    assertEquals(depName, resp[0].getDeploymentName());

    final Deployment dep = client.waitForCatalogDeployment(resp[0].getDeploymentId(), 300000);
    assertNotNull(dep);
    assertNotNull(dep.getId());
    assertEquals(resp[0].getDeploymentId(), dep.getId().toString());

    final List<String> ip =
        client.waitForIPAddresses(resp[0].getDeploymentId(), "UbuntuMachine", 300000);
    assertNotNull(ip);
    assertEquals(1, ip.size());
    assertTrue(ipPattern.matcher(ip.get(0)).matches());

    final DeploymentRequest dr = client.deleteCatalogDeployment(dep.getId().toString());
    assertNotNull(dr);
    assertNotNull(dr.getId());

    final DeploymentRequest deploymentRequest =
        client.waitForRequestCompletion(dr.getId().toString(), 300000);
    assertNotNull(deploymentRequest);
    assertNotNull(deploymentRequest.getId());
  }

  @Test
  public void testDeploymentMocked() throws Exception {
    final Gson gson = new Gson();

    // Load templates
    final ProjectResult wantedProject =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/ProjectResult.json"), ProjectResult.class);
    final CatalogItemRequestResponse[] wantedResponse =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/CatalogItemRequestResponse.json"),
            CatalogItemRequestResponse[].class);
    final CatalogItemRequest wantedRequest =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/CatalogItemRequest.json"),
            CatalogItemRequest.class);
    final PageOfCatalogItem wantedCatalogItem =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/PageOfCatalogItem.json"), PageOfCatalogItem.class);

    // Set up mocking
    final VraClient mocked = mock(VraClient.class);
    when(mocked.get(eq("/iaas/api/projects"), any(), eq(ProjectResult.class)))
        .thenReturn(wantedProject);
    when(mocked.get(eq("/catalog/api/items"), any(), eq(PageOfCatalogItem.class)))
        .thenReturn(wantedCatalogItem);
    when(mocked.post(eq("/catalog/api/items/" + catalogItemId + "/request"), any(), any(), any()))
        .thenReturn(wantedResponse);
    final VraApi client = new VraApi(mocked);
    final String depName = wantedResponse[0].getDeploymentName();
    final CatalogItemRequestResponse[] resp =
        client.deployFromCatalog(
            catalogItemName, version, projectName, depName, "Some reason", null, 1);
    verify(mocked, times(1))
        .post(
            eq("/catalog/api/items/" + catalogItemId + "/request"),
            any(),
            eq(wantedRequest),
            eq(CatalogItemRequestResponse[].class));
    assertEquals(1, resp.length);
    assertNotNull(resp[0]);
    assertNotNull(resp[0].getDeploymentId());
    assertEquals(depName, resp[0].getDeploymentName());
  }

  @Test
  public void testDeploymentActionMocked() throws Exception {
    // Load templates
    final Gson gson = new Gson();
    final ResourceActionRequest action =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/ResourceActionRequest.json"),
            ResourceActionRequest.class);
    final DeploymentRequest wantedRequest =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/DeploymentRequestPending.json"),
            DeploymentRequest.class);

    // Set up mocking
    final VraClient mocked = mock(VraClient.class);
    final VraApi client = new VraApi(mocked);
    when(mocked.post(
            eq("/deployment/api/deployments/" + deploymentId + "/requests"),
            any(),
            eq(action),
            eq(DeploymentRequest.class)))
        .thenReturn(wantedRequest);
    final DeploymentRequest dr =
        client.submitDeploymentAction(
            deploymentId,
            action.getActionId(),
            action.getReason(),
            (Map<String, Object>) action.getInputs());
    verify(mocked, times(1))
        .post(
            eq("/deployment/api/deployments/" + deploymentId + "/requests"),
            any(),
            eq(action),
            eq(DeploymentRequest.class));
    assertEquals(wantedRequest.getActionId(), dr.getActionId());
    assertEquals(wantedRequest.getInputs(), dr.getInputs());
    assertEquals(wantedRequest.getStatus(), dr.getStatus());
  }

  private DeploymentRequest waitForCompletionWithTimeout(
      final long delay, final long timeout, final int invocations) throws Exception {
    // Load templates
    final Gson gson = new Gson();
    final DeploymentRequest pendingRequest =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/DeploymentRequestPending.json"),
            DeploymentRequest.class);
    // Load templates
    final DeploymentRequest readyRequest =
        gson.fromJson(
            FileUtils.loadResource("/apiresults/DeploymentRequestSuccess.json"),
            DeploymentRequest.class);

    // Set up mocking
    final VraClient mocked = mock(VraClient.class);
    final VraApi client = new VraApi(mocked);
    final Delay<DeploymentRequest> provider = new Delay<>(delay, pendingRequest, readyRequest);
    doAnswer(i -> provider.poll())
        .when(mocked)
        .get(eq("/deployment/api/requests/" + deploymentId), any(), eq(DeploymentRequest.class));
    final DeploymentRequest dr = client.waitForRequestCompletion(deploymentId, timeout);
    verify(mocked, times(invocations))
        .get(eq("/deployment/api/requests/" + deploymentId), any(), eq(DeploymentRequest.class));
    return dr;
  }

  @Test
  public void testWaitForRequestCompletionHappyPath() throws Exception {
    final DeploymentRequest dr = waitForCompletionWithTimeout(30000, 31000, 2);
    assertEquals("SUCCESSFUL", dr.getStatus().getValue());
  }

  @Test
  public void testWaitForRequestCompletionTimeout() throws Exception {
    assertThrows(
        "Should time out",
        TimeoutException.class,
        () -> waitForCompletionWithTimeout(31000, 30000, 2));
  }

  private static class Delay<T> {
    private final long delay;
    private final long start = System.currentTimeMillis();
    private final T pending;
    private final T ready;

    public Delay(final long delay, final T pending, final T ready) {
      this.delay = delay;
      this.pending = pending;
      this.ready = ready;
    }

    public T poll() throws Exception {
      return System.currentTimeMillis() - start > delay ? ready : pending;
    }
  }
}
