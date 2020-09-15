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

package com.vmware.vra.jenkinsplugin.pipeline;

import static com.vmware.vra.jenkinsplugin.util.MapUtils.mappify;

import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.model.catalog.Resource;
import com.vmware.vra.jenkinsplugin.model.catalog.ResourceActionRequest;
import com.vmware.vra.jenkinsplugin.model.deployment.DeploymentRequest;
import com.vmware.vra.jenkinsplugin.util.MapUtils;
import com.vmware.vra.jenkinsplugin.vra.VraApi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

public class RunActionExecution extends SynchronousNonBlockingStepExecution<Object> {
  private static final long serialVersionUID = -5637803299301492970L;

  private final RunActionStep step;

  public RunActionExecution(final StepContext context, final RunActionStep step) {
    super(context);
    this.step = step;
  }

  @Override
  protected Object run() throws Exception {
    final VraApi client = step.getClient();
    final String resourceName = step.getResourceName();
    if (StringUtils.isBlank(resourceName)) {
      final DeploymentRequest dr =
          client.submitDeploymentAction(
              step.resolveDeploymentId(),
              step.getActionId(),
              step.getReason(),
              step.resolveInputs());
      return mappify(
          Collections.singleton(
              client.waitForRequestCompletion(dr.getId().toString(), step.getTimeout() * 1000)));
    }

    // Resource name was specified. Run action against all matching resources.
    final Deployment dep = client.getCatalogDeploymentById(step.resolveDeploymentId(), true);
    if (dep == null) {
      throw new IllegalArgumentException("Deployment does not exist: " + step.getDeploymentId());
    }
    final Pattern resourcePattern = VraApi.getResourcePattern(resourceName);
    final List<DeploymentRequest> drs = new ArrayList<>();
    for (final Resource r : dep.getResources()) {
      if (!resourcePattern.matcher(r.getName()).matches()) {
        continue;
      }
      drs.add(
          client.submitResourceAction(
              step.resolveDeploymentId(),
              r.getId().toString(),
              step.getActionId(),
              step.getReason(),
              step.resolveInputs()));
    }
    if (drs.size() == 0) {
      throw new IllegalArgumentException(
          "Resource " + resourceName + " was not part of the deployment");
    }
    final List<ResourceActionRequest> result = new ArrayList<>(drs.size());
    return MapUtils.mappify(
        client.waitForRequestCompletion(
            drs.stream().map(dr -> dr.getId().toString()).collect(Collectors.toList()),
            step.getTimeout() * 1000));
  }
}
