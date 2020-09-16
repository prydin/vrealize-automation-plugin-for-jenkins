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

import com.google.common.collect.ImmutableSet;
import com.vmware.vra.jenkinsplugin.util.MapUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class RunActionStep extends DeploymentAwareStep implements Serializable, StepWithInputs {
  private static final long serialVersionUID = 7632401023113802055L;

  private String inputs;

  private Map<String, Object> inputMap;

  private String resourceName;

  private String actionId;

  private String reason;

  private long timeout = 300;

  @DataBoundConstructor
  public RunActionStep() {}

  @Override
  public String getInputs() {
    return inputs;
  }

  @DataBoundSetter
  public void setInputs(final String inputs) {
    this.inputs = inputs;
  }

  public long getTimeout() {
    return timeout;
  }

  @DataBoundSetter
  public void setTimeout(final long timeout) {
    this.timeout = timeout;
  }

  @Override
  public Map<String, Object> getInputMap() {
    return inputMap;
  }

  @DataBoundSetter
  public void setInputMap(final Map<String, Object> inputMap) {
    this.inputMap = inputMap;
  }

  public String getActionId() {
    return actionId;
  }

  @DataBoundSetter
  public void setActionId(final String actionId) {
    this.actionId = actionId;
  }

  public String getReason() {
    return reason;
  }

  @DataBoundSetter
  public void setReason(final String reason) {
    this.reason = reason;
  }

  public String getResourceName() {
    return resourceName;
  }

  @DataBoundSetter
  public void setResourceName(final String resourceName) {
    this.resourceName = resourceName;
  }

  @Override
  public StepExecution start(final StepContext stepContext) throws Exception {
    return new RunActionExecution(stepContext, this);
  }

  @Override
  public Map<String, Object> resolveInputs() {
    return MapUtils.resolveFromStep(this);
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
      return "vraRunAction";
    }

    @Override
    @Nonnull
    public String getDisplayName() {
      return "vRA - Run Action";
    }
  }
}
