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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.vra.VRAException;
import org.kohsuke.stapler.DataBoundSetter;

public abstract class DeploymentAwareStep extends AbstractStep {
  private static final long serialVersionUID = 7479667613779694008L;
  protected String deploymentId;

  protected String deploymentName;

  public String getDeploymentId() {
    return deploymentId;
  }

  @DataBoundSetter
  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = deploymentId;
  }

  public String getDeploymentName() {
    return deploymentName;
  }

  @DataBoundSetter
  public void setDeploymentName(final String deploymentName) {
    this.deploymentName = deploymentName;
  }

  public String resolveDeploymentId() throws VRAException {
    final String depId = getDeploymentId();
    final String depName = getDeploymentName();
    if (isNotBlank(depId)) {
      return depId;
    }
    final Deployment dep = getClient().getCatalogDeploymentByName(depName, false);
    if (dep == null) {
      throw new IllegalArgumentException("Deployment " + depName + " does not exist");
    }
    return dep.getId().toString();
  }

  protected void validate() throws IllegalArgumentException {
    if (isNotBlank(getDeploymentId()) && isNotBlank(getDeploymentName())) {
      throw new IllegalArgumentException(
          "'deploymentId' and 'deploymentName' are mutually exclusive");
    }
    if (isBlank(getDeploymentId()) && isBlank(getDeploymentName())) {
      throw new IllegalArgumentException(
          "Either 'deploymentId' or 'deploymentName' must be specified");
    }
  }
}
