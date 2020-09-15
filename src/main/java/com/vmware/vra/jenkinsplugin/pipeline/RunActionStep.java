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
import com.google.gson.Gson;
import com.vmware.vra.jenkinsplugin.vra.VRAException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class RunActionStep extends DeploymentAwareStep {
  private static final long serialVersionUID = 7632401023113802055L;

  private String inputs;

  private Map<String, String> inputMap;

  private String resourceName;

  private String actionId;

  private String reason;

  private long timeout = 300;

  @DataBoundConstructor
  public RunActionStep() {}

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

  public Map<String, String> getInputMap() {
    return inputMap;
  }

  @DataBoundSetter
  public void setInputMap(final Map<String, String> inputMap) {
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

  public Map<String, String> resolveInputs() throws VRAException {
    if (getInputMap() != null && getInputs() != null) {
      throw new VRAException("Parameters 'input' and 'inputMap' are mutually exclusive");
    }
    if (getInputMap() != null) {
      return getInputMap();
    } else if (StringUtils.isNotBlank(getInputs())) {
      return new Gson().fromJson(getInputs(), Map.class);
    }
    return Collections.EMPTY_MAP;
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
