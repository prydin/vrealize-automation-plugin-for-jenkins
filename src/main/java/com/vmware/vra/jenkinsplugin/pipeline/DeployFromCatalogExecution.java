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

import static com.vmware.vra.jenkinsplugin.util.JSONUtils.fromJsonToMap;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.vmware.vra.jenkinsplugin.model.catalog.CatalogItemRequestResponse;
import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.util.MapUtils;
import com.vmware.vra.jenkinsplugin.util.ValueCheckers;
import com.vmware.vra.jenkinsplugin.vra.VraApi;
import hudson.EnvVars;
import hudson.model.TaskListener;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.yaml.snakeyaml.Yaml;

public class DeployFromCatalogExecution extends SynchronousNonBlockingStepExecution<Object>
    implements Serializable {
  private static final long serialVersionUID = -2997964521533971915L;
  private final DeployFromCatalogStep step;

  protected DeployFromCatalogExecution(
      @Nonnull final DeployFromCatalogStep step, @Nonnull final StepContext context) {
    super(context);
    this.step = step;
  }

  private static String processDeploymentName(final String name) {
    if (name == null) {
      return "Jenkins-" + UUID.randomUUID().toString();
    }
    return name.replace("#", UUID.randomUUID().toString());
  }

  @Override
  protected Object run() throws Exception {
    final PrintStream log = getContext().get(TaskListener.class).getLogger();

    final VraApi client = step.getClient();
    final CatalogItemRequestResponse[] response;
    final EnvVars env = getContext().get(EnvVars.class);
    final String config = env.expand(step.getConfig());
    if (isNotBlank(config)) {
      if (isNotBlank(step.getCatalogItemName())
          || isNotBlank(step.getVersion())
          || isNotBlank(step.getProjectName())
          || isNotBlank(step.getReason())
          || isNotBlank(step.getVersion())
          || isNotBlank(step.getInputs())) {
        throw new IllegalArgumentException(
            "The 'config' property is mutually exclusive with all other deployment properties");
      }
      final Config c;
      if (step.getConfigFormat().toLowerCase().equals("json")) {
        // c = fromJson(config, Config.class);
        // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
        c = loadFromJsonWorkaround(config);
      } else if (step.getConfigFormat().toLowerCase().equals("yaml")) {
        // final Yaml y = new Yaml();
        // c = y.loadAs(config, Config.class);
        // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
        c = loadFromYamlWorkaround(config);
      } else {
        throw new IllegalArgumentException("configFormat must be either 'json' or 'yaml'");
      }
      response =
          client.deployFromCatalog(
              ValueCheckers.notBlank(c.getCatalogItemName(), "catalogItemName"),
              c.getVersion(),
              ValueCheckers.notBlank(c.getProjectName(), "projectName"),
              processDeploymentName(c.getDeploymentName()),
              c.getReason(),
              c.getInputs() != null ? c.getInputs() : new HashMap<>(),
              c.getCount());
    } else {
      final String inputs = step.getInputs();
      response =
          client.deployFromCatalog(
              ValueCheckers.notBlank(step.getCatalogItemName(), "catalogItemName"),
              step.getVersion(),
              ValueCheckers.notBlank(step.getProjectName(), "projectName"),
              processDeploymentName(step.getDeploymentName()),
              step.getReason(),
              isNotBlank(inputs) ? fromJsonToMap(inputs) : new HashMap<>(),
              step.getCount());
    }
    log.println("Successfully requested deployment. Deployment ids: " + Arrays.toString(response));
    log.println("Waiting for deployment to complete");

    // Wait for all deployments to finish
    final Deployment[] deps = new Deployment[response.length];
    int i = 0;
    for (final CatalogItemRequestResponse cirr : response) {
      deps[i++] = client.waitForCatalogDeployment(cirr.getDeploymentId(), step.getTimeout() * 1000);
      log.println(
          "Deployment " + cirr.getDeploymentName() + "(" + cirr.getDeploymentId() + ") finished");
    }
    log.println("All deployments finished!");
    return MapUtils.mappify(deps);
  }

  // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
  private static Config loadFromJsonWorkaround(final String content) {
    final Map<String, Object> map = fromJsonToMap(content);
    return buildConfigFromMap(map);
  }

  // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
  private static Config loadFromYamlWorkaround(final String content) {
    final Yaml y = new Yaml();
    return buildConfigFromMap(y.loadAs(content, Map.class));
  }

  // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
  private static Config buildConfigFromMap(final Map<String, Object> map) {
    final Config c = new Config();
    c.catalogItemName = toString(map.get("catalogItemName"));
    c.count = toInt(map.get("count"));
    c.deploymentName = toString(map.get("deploymentName"));
    c.inputs = (Map<String, Object>) map.get("inputs");
    c.projectName = toString(map.get("projectName"));
    c.reason = toString(map.get("reason"));
    c.version = toString(map.get("version"));
    return c;
  }

  // Workaround for https://issues.jenkins.io/browse/JENKINS-64498. Remove when fixed!
  private static String toString(final Object o) {
    if (o == null) {
      return "";
    }
    return o.toString();
  }

  private static int toInt(final Object o) {
    if (o instanceof Number) {
      return ((Number) o).intValue();
    }
    if (o instanceof String) {
      return Integer.parseInt((String) o);
    }
    throw new IllegalArgumentException(
        "Object of class " + o.getClass().getName() + " cannot be converted to int");
  }

  public static final class Config { // Must be public due to SnakeYAML access
    String catalogItemName;
    String version;
    String projectName;
    String deploymentName;
    String reason;
    Map<String, Object> inputs;
    int count = 1;

    public Config() {}

    public String getCatalogItemName() {
      return catalogItemName;
    }

    public void setCatalogItemName(final String catalogItemName) {
      this.catalogItemName = catalogItemName;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(final String version) {
      this.version = version;
    }

    public String getProjectName() {
      return projectName;
    }

    public void setProjectName(final String projectName) {
      this.projectName = projectName;
    }

    public String getDeploymentName() {
      return deploymentName;
    }

    public void setDeploymentName(final String deploymentName) {
      this.deploymentName = deploymentName;
    }

    public String getReason() {
      return reason;
    }

    public void setReason(final String reason) {
      this.reason = reason;
    }

    public Map<String, Object> getInputs() {
      return inputs;
    }

    public void setInputs(final Map<String, Object> inputs) {
      this.inputs = inputs;
    }

    public int getCount() {
      return count;
    }

    public void setCount(final int count) {
      this.count = count;
    }
  }
}
