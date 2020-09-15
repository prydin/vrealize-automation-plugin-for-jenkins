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

import com.google.errorprone.annotations.DoNotCall;
import com.vmware.vra.jenkinsplugin.GlobalVRAConfiguration;
import com.vmware.vra.jenkinsplugin.util.SecretHelper;
import com.vmware.vra.jenkinsplugin.vra.VRAException;
import com.vmware.vra.jenkinsplugin.vra.VraApi;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundSetter;

public abstract class AbstractStep extends Step implements Serializable {
  private static final long serialVersionUID = 4802272043548740707L;
  protected String vraURL;

  protected String token;

  private VraApi cachedClient;

  public AbstractStep() {}

  public synchronized VraApi getClient() throws VRAException {
    if (cachedClient != null) {
      return cachedClient;
    }
    return cachedClient = new VraApi(resolveVraURL(), resolveToken());
  }

  @DoNotCall("use resolveVraURL instead!")
  public String getVraURL() {
    return vraURL;
  }

  @DataBoundSetter
  public void setVraURL(final String vraURL) {
    this.vraURL = vraURL;
  }

  public String resolveVraURL() {
    if (StringUtils.isNotBlank(vraURL)) {
      return vraURL;
    }
    return GlobalVRAConfiguration.get().getVraURL();
  }

  @DoNotCall("use resolveToken instead!")
  public String getToken() {
    return token;
  }

  @DataBoundSetter
  public void setToken(final String token) {
    this.token = token;
  }

  public String resolveToken() {
    // If token is specified locally, got get it. Otherwise, try to get it from the global config.
    if (StringUtils.isNotBlank(token)) {
      return token;
    }
    final String credId = GlobalVRAConfiguration.get().getCredentialId();
    if (credId == null) {
      return null;
    }
    return SecretHelper.getSecretFor(credId).orElse(null);
  }
}
