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

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class DeployFromCatalogStep extends AbstractStep implements Serializable {
  private static final long serialVersionUID = -4841698058313077987L;
  private int timeout = 300;
  private String projectName;
  private String catalogItemName;
  private String version;
  private String deploymentName;
  private String reason;
  private String inputs;
  private String config;
  private String configFormat = "json";
  private int count = 1;

  @DataBoundConstructor
  public DeployFromCatalogStep() {}

  @Override
  public StepExecution start(final StepContext stepContext) {
    return new DeployFromCatalogExecution(this, stepContext);
  }

  public String getProjectName() {
    return projectName;
  }

  @DataBoundSetter
  public void setProjectName(final String projectName) {
    this.projectName = projectName;
  }

  public String getCatalogItemName() {
    return catalogItemName;
  }

  @DataBoundSetter
  public void setCatalogItemName(final String catalogItemName) {
    this.catalogItemName = catalogItemName;
  }

  public String getVersion() {
    return version;
  }

  @DataBoundSetter
  public void setVersion(final String version) {
    this.version = version;
  }

  public String getDeploymentName() {
    return deploymentName;
  }

  @DataBoundSetter
  public void setDeploymentName(final String deploymentName) {
    this.deploymentName = deploymentName;
  }

  public String getReason() {
    return reason;
  }

  @DataBoundSetter
  public void setReason(final String reason) {
    this.reason = reason;
  }

  public String getInputs() {
    return inputs;
  }

  @DataBoundSetter
  public void setInputs(final String inputs) {
    this.inputs = inputs;
  }

  public int getCount() {
    return count;
  }

  @DataBoundSetter
  public void setCount(final int count) {
    this.count = count;
  }

  public int getTimeout() {
    return timeout;
  }

  @DataBoundSetter
  public void setTimeout(final int timeout) {
    this.timeout = timeout;
  }

  public String getConfig() {
    return config;
  }

  @DataBoundSetter
  public void setConfig(final String config) {
    this.config = config;
  }

  public String getConfigFormat() {
    return configFormat;
  }

  @DataBoundSetter
  public void setConfigFormat(final String configFormat) {
    this.configFormat = configFormat;
  }

  @Extension
  public static class DescriptorImpl extends StepDescriptor {

    public DescriptorImpl() {}

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "vraDeployFromCatalog";
    }

    @Override
    @Nonnull
    public String getDisplayName() {
      return "vRA - Deploy from catalog";
    }

    public ListBoxModel doFillConfigFormatItems() {
      final StandardListBoxModel result = new StandardListBoxModel();
      result.add(new ListBoxModel.Option("json", "json"));
      result.add(new ListBoxModel.Option("yaml", "yaml"));
      return result;
    }
  }
}
