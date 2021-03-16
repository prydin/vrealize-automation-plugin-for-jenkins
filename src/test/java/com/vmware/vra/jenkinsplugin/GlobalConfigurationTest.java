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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.vmware.vra.jenkinsplugin.testutils.HTMLUtils;
import hudson.util.Secret;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.RestartableJenkinsRule;

public class GlobalConfigurationTest {

  @Rule public RestartableJenkinsRule rr = new RestartableJenkinsRule();

  /**
   * Tries to exercise enough code paths to catch common mistakes:
   *
   * <ul>
   *   <li>missing {@code load}
   *   <li>missing {@code save}
   *   <li>misnamed or absent getter/setter
   *   <li>misnamed {@code textbox}
   * </ul>
   */
  @Test
  public void uiAndStorage() {
    rr.then(
        r -> {
          final StringCredentials vraCredentials =
              new StringCredentialsImpl(
                  CredentialsScope.GLOBAL, "vraToken", null, Secret.fromString("token"));
          CredentialsProvider.lookupStores(r.jenkins)
              .iterator()
              .next()
              .addCredentials(Domain.global(), vraCredentials);

          assertNull("not set initially", GlobalVRAConfiguration.get().getVraURL());
          final HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
          final HtmlTextInput textbox = config.getInputByName("_.vraURL");
          final HtmlSelect credential = config.getSelectByName("_.credentialId");

          HTMLUtils.setNamedOption(credential, "vraToken");
          textbox.setText("hello");

          r.submit(config);
          assertEquals(
              "global config page let us edit it",
              "hello",
              GlobalVRAConfiguration.get().getVraURL());
          assertEquals(
              "credential check", "vraToken", GlobalVRAConfiguration.get().getCredentialId());
        });
    rr.then(
        r -> {
          assertEquals(
              "still there after restart of Jenkins",
              "hello",
              GlobalVRAConfiguration.get().getVraURL());
          assertEquals(
              "credential check", "vraToken", GlobalVRAConfiguration.get().getCredentialId());
        });
  }
}
