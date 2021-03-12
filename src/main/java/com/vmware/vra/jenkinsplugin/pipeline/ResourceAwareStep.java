package com.vmware.vra.jenkinsplugin.pipeline;

import com.vmware.vra.jenkinsplugin.model.catalog.Deployment;
import com.vmware.vra.jenkinsplugin.model.catalog.Resource;
import com.vmware.vra.jenkinsplugin.vra.VRAException;
import com.vmware.vra.jenkinsplugin.vra.VraApi;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.kohsuke.stapler.DataBoundSetter;

public abstract class ResourceAwareStep extends DeploymentAwareStep {
  private static final long serialVersionUID = 8646412880207167926L;
  private String resourceName;

  public String getResourceName() {
    return resourceName;
  }

  @DataBoundSetter
  public void setResourceName(final String resourceName) {
    this.resourceName = resourceName;
  }

  protected List<UUID> resolveResourceIds() throws VRAException {
    final Deployment dep = getClient().getCatalogDeploymentById(resolveDeploymentId(), true);
    if (dep == null) {
      throw new IllegalArgumentException("Deployment does not exist: " + getDeploymentId());
    }
    final Pattern resourcePattern = VraApi.getResourcePattern(resourceName);
    final List<UUID> ids = new ArrayList<>();
    for (final Resource r : dep.getResources()) {
      if (resourcePattern.matcher(r.getName()).matches()) {
        ids.add(r.getId());
      }
    }
    return ids;
  }
}
