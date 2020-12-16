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
import static org.junit.Assert.assertTrue;

import hudson.util.StreamTaskListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jenkinsci.plugins.structs.describable.DescribableModel;
import org.jenkinsci.plugins.workflow.steps.StepConfigTester;
import org.jvnet.hudson.test.RestartableJenkinsRule;

public abstract class DeploymentAwareStepTest<T extends DeploymentAwareStep> {
  private final Class<T> clazz;

  public DeploymentAwareStepTest(final Class<T> clazz) {
    this.clazz = clazz;
  }

  protected void runAllStandardConfigs(final RestartableJenkinsRule rr) {
    runConfigRoundtripWithId(rr, Collections.emptyMap());
    runConfigRoundtripWithName(rr, Collections.emptyMap());
    runConfigRoundtripWithUsername(rr, Collections.emptyMap());
  }

  protected void runConfigRoundtripWithId(
      final RestartableJenkinsRule rr, final Map<String, Object> additional) {
    rr.then(
        r -> {
          final StepConfigTester sct = new StepConfigTester(rr.j);
          final Map<String, Object> config = new HashMap<>();
          config.put("vraURL", "vraURL");
          config.put("token", "token");
          config.put("trustSelfSignedCert", true);
          config.put("deploymentId", "deploymentId");
          config.putAll(additional);
          final DescribableModel<T> model = new DescribableModel<>(clazz);
          final T step =
              sct.configRoundTrip(model.instantiate(config, StreamTaskListener.fromStderr()));
          assertEquals("vraURL", step.getVraURL());
          assertEquals("token", step.getToken());
          assertEquals("deploymentId", step.getDeploymentId());
          additional.forEach((k, v) -> assertEquals(v, getFromBean(step, k)));
          assertTrue(step.isTrustSelfSignedCert());
          model.uninstantiate2_(step);
        });
  }

  protected void runConfigRoundtripWithUsername(
      final RestartableJenkinsRule rr, final Map<String, Object> additional) {
    rr.then(
        r -> {
          final StepConfigTester sct = new StepConfigTester(rr.j);
          final Map<String, Object> config = new HashMap<>();
          config.put("vraURL", "vraURL");
          config.put("username", "username");
          config.put("password", "password");
          config.put("trustSelfSignedCert", true);
          config.put("deploymentId", "deploymentId");
          config.putAll(additional);
          final DescribableModel<T> model = new DescribableModel<>(clazz);
          final T step =
              sct.configRoundTrip(model.instantiate(config, StreamTaskListener.fromStderr()));
          assertEquals("vraURL", step.getVraURL());
          assertEquals("username", step.getUsername());
          assertEquals("password", step.getPassword());
          assertEquals("deploymentId", step.getDeploymentId());
          assertTrue(step.isTrustSelfSignedCert());
          additional.forEach((k, v) -> assertEquals(v, getFromBean(step, k)));
          model.uninstantiate2_(step);
        });
  }

  protected void runConfigRoundtripWithName(
      final RestartableJenkinsRule rr, final Map<String, Object> additional) {
    rr.then(
        r -> {
          final StepConfigTester sct = new StepConfigTester(rr.j);
          final Map<String, Object> config = new HashMap<>();
          config.put("vraURL", "vraURL");
          config.put("token", "token");
          config.put("deploymentName", "deploymentName");
          config.putAll(additional);
          final DescribableModel<T> model = new DescribableModel<>(clazz);
          final T step =
              sct.configRoundTrip(model.instantiate(config, StreamTaskListener.fromStderr()));
          assertEquals("vraURL", step.getVraURL());
          assertEquals("token", step.getToken());
          assertEquals("deploymentName", step.getDeploymentName());
          additional.forEach((k, v) -> assertEquals(v, getFromBean(step, k)));
          model.uninstantiate2_(step);
        });
  }

  private static Object getFromBean(final Object bean, final String key) {
    final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
    try {
      final Method method = bean.getClass().getMethod(methodName);
      return method.invoke(bean);
    } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Could not find getter " + methodName, e);
    }
  }
}
