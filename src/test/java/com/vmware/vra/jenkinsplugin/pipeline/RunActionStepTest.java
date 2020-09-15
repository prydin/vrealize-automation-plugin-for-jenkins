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

import static org.junit.Assert.assertEquals;

import hudson.util.StreamTaskListener;
import java.util.HashMap;
import java.util.Map;
import org.jenkinsci.plugins.structs.describable.DescribableModel;
import org.jenkinsci.plugins.workflow.steps.StepConfigTester;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.RestartableJenkinsRule;

public class RunActionStepTest extends DeploymentAwareStepTest<RunActionStep> {
  private static final long serialVersionUID = -3059689026336272954L;
  @Rule public RestartableJenkinsRule rr = new RestartableJenkinsRule();

  public RunActionStepTest() {
    super(RunActionStep.class);
  }

  @Test
  public void testFullConfigRoundtrip() {
    rr.then(
        r -> {
          final StepConfigTester sct = new StepConfigTester(rr.j);
          final Map<String, Object> config = new HashMap<>();
          config.put("vraURL", "vraURL");
          config.put("token", "token");
          config.put("deploymentId", "deploymentId");
          config.put("timeout", 1L);
          config.put("actionId", "actionId");
          config.put("resourceName", "resourceName");
          config.put("reason", "reason");

          final DescribableModel<RunActionStep> model = new DescribableModel<>(RunActionStep.class);
          RunActionStep step = model.instantiate(config, StreamTaskListener.fromStderr());
          step = sct.configRoundTrip(step);
          assertEquals("vraURL", step.getVraURL());
          assertEquals("token", step.getToken());
          assertEquals("deploymentId", step.getDeploymentId());
          assertEquals(1L, step.getTimeout());
          assertEquals("actionId", step.getActionId());
          assertEquals("resourceName", step.getResourceName());
          assertEquals("reason", step.getReason());
          model.uninstantiate2_(step);
        });
  }
}
