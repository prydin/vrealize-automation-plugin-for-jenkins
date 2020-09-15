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

package com.vmware.vra.jenkinsplugin;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import java.io.Serializable;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

@Extension
public class GlobalVRAConfiguration extends GlobalConfiguration implements Serializable {

  private static final long serialVersionUID = -8311413991909048173L;
  private String vraURL;

  private String credentialId;

  public GlobalVRAConfiguration() {
    // When Jenkins is restarted, load any saved configuration from disk.
    load();
  }

  public static GlobalVRAConfiguration get() {
    return jenkins.model.GlobalConfiguration.all().get(GlobalVRAConfiguration.class);
  }

  public String getVraURL() {
    return vraURL;
  }

  @DataBoundSetter
  public void setVraURL(final String vraURL) {
    this.vraURL = vraURL;
    save();
  }

  public String getCredentialId() {
    return credentialId;
  }

  @DataBoundSetter
  public void setCredentialId(final String credentialId) {
    this.credentialId = credentialId;
    save();
  }

  public ListBoxModel doFillCredentialIdItems(
      @AncestorInPath final Item item, @QueryParameter final String credentialId) {
    final StandardListBoxModel result = new StandardListBoxModel();
    final Jenkins instance = Jenkins.get();
    if (item == null) {
      if (!instance.hasPermission(Jenkins.ADMINISTER)) {
        return result.includeCurrentValue(credentialId);
      }
    } else {
      if (!item.hasPermission(Item.EXTENDED_READ)
          && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
        return result.includeCurrentValue(credentialId);
      }
    }
    return result
        .includeEmptyValue()
        .includeMatchingAs(
            ACL.SYSTEM,
            instance,
            StringCredentials.class,
            URIRequirementBuilder.fromUri(getVraURL()).build(),
            CredentialsMatchers.always())
        .includeCurrentValue(credentialId);
  }
}
