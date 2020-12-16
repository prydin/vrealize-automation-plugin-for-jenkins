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

package com.vmware.vra.jenkinsplugin.vra;

import static com.vmware.vra.jenkinsplugin.util.JSONUtils.fromJson;
import static com.vmware.vra.jenkinsplugin.util.JSONUtils.toJson;

import com.vmware.vra.jenkinsplugin.model.AuthenticationRequest;
import com.vmware.vra.jenkinsplugin.model.AuthenticationResponse;
import com.vmware.vra.jenkinsplugin.model.UserPasswordAuthenticationRequest;
import com.vmware.vra.jenkinsplugin.model.UserPasswordAuthenticationResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class VraClient implements Serializable {
  static final String API_VERSION = "2019-09-12";
  private static final long serialVersionUID = 3442278892595463523L;
  private final String baseUrl;
  private final boolean trustSelfSignedCert;
  private String refreshToken;

  protected VraClient(final String baseUrl, final boolean trustSelfSignedCert) {
    this.baseUrl = baseUrl;
    this.trustSelfSignedCert = trustSelfSignedCert;
  }

  public VraClient(final String baseUrl, final String token, final boolean trustSelfSignedCert)
      throws VRAException {
    this(baseUrl, trustSelfSignedCert);
    authenticate(token);
  }

  public VraClient(
      final String baseUrl,
      final String domain,
      final String username,
      final String password,
      final boolean trustSelfSignedCert)
      throws VRAException {
    this(baseUrl, trustSelfSignedCert);
    authenticate(domain, username, password);
  }

  static String readAll(final InputStream in) throws IOException {
    final InputStreamReader rdr = new InputStreamReader(in, Charset.forName("UTF-8"));
    final StringBuilder sb = new StringBuilder();
    final char[] buffer = new char[8192];
    int n;
    while ((n = rdr.read(buffer)) > 0) {
      sb.append(buffer, 0, n);
    }
    return sb.toString();
  }

  private static String buildQuery(final Map<String, String> queries)
      throws UnsupportedEncodingException {
    if (queries == null) {
      return "";
    }
    final StringBuilder s = new StringBuilder("?apiVersion=" + VraClient.API_VERSION);
    for (final Map.Entry<String, String> q : queries.entrySet()) {
      s.append('&');
      s.append(q.getKey())
          .append("=")
          .append(URLEncoder.encode(q.getValue(), Charset.defaultCharset().name()));
    }
    return s.toString();
  }

  protected void authenticate(final String token) throws VRAException {
    final AuthenticationResponse resp =
        post(
            "/iaas/api/login",
            null,
            new AuthenticationRequest(token),
            AuthenticationResponse.class);
    refreshToken = resp.getToken();
  }

  protected void authenticate(final String domain, final String username, final String password)
      throws VRAException {
    final UserPasswordAuthenticationResponse resp =
        post(
            "/csp/gateway/am/api/login?access_token",
            null,
            new UserPasswordAuthenticationRequest(domain, username, password),
            UserPasswordAuthenticationResponse.class);
    authenticate(resp.getRefresh_token());
  }

  public <R> R post(
      final String url,
      final Map<String, String> query,
      final Object request,
      final Class<R> responseClass)
      throws VRAException {
    try {
      final String content = post(url, query, toJson(request));
      return fromJson(content, responseClass);
    } catch (final IOException e) {
      throw new VRAException(e);
    }
  }

  private CloseableHttpClient getClient() throws VRAException {
    try {
      final SSLContextBuilder builder = new SSLContextBuilder();
      if (trustSelfSignedCert) {
        final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        builder.loadTrustMaterial(null, acceptingTrustStrategy);
      }
      final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
      return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    } catch (final KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
      throw new VRAException(e);
    }
  }

  public String post(final String url, final Map<String, String> query, final String payload)
      throws IOException, VRAException {
    final HttpPost postRequest = new HttpPost(baseUrl + url + buildQuery(query));
    final StringEntity input = new StringEntity(payload);
    input.setContentType("application/json");
    postRequest.setEntity(input);
    return executeRequest(postRequest);
  }

  public String get(final String url, final Map<String, String> query)
      throws IOException, VRAException {
    final HttpGet rq = new HttpGet(baseUrl + url + buildQuery(query));
    return executeRequest(rq);
  }

  public String delete(final String url, final Map<String, String> query)
      throws IOException, VRAException {
    final HttpDelete rq = new HttpDelete(baseUrl + url + buildQuery(query));
    return executeRequest(rq);
  }

  public String executeRequest(final @Nonnull HttpUriRequest rq) throws IOException, VRAException {
    try (final CloseableHttpClient client = getClient()) {
      rq.setHeader("Accept", "application/json; charset=utf-8");
      if (StringUtils.isNotBlank(refreshToken)) {
        final String authorization = "Bearer " + refreshToken;
        rq.setHeader("Authorization", authorization);
      }

      // Send and handle response
      final HttpResponse resp = client.execute(rq);
      if (resp.getStatusLine().getStatusCode() != 200) {
        throw new VRAException(resp.getStatusLine().toString() + " URL:" + rq.getURI().toString());
      }
      return readAll(resp.getEntity().getContent());
    }
  }

  public <R> R get(final String url, final Map<String, String> query, final Class<R> responseClass)
      throws VRAException {
    try {
      final String content = get(url, query);
      return fromJson(content, responseClass);
    } catch (final IOException e) {
      throw new VRAException(e);
    }
  }

  public <R> R delete(
      final String url, final Map<String, String> query, final Class<R> responseClass)
      throws VRAException {
    try {
      final String content = delete(url, query);
      return fromJson(content, responseClass);
    } catch (final IOException e) {
      throw new VRAException(e);
    }
  }
}
