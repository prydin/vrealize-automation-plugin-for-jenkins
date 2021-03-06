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

/*
 * VMware Service Broker API
 * A multi-cloud API for Cloud Automation Services
 *
 * OpenAPI spec version: 2020-08-25
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.vmware.vra.jenkinsplugin.model.catalog;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/** TagBasedOneTimeMetering */
@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
    date = "2020-09-09T18:27:41.063635-04:00[America/New_York]")
public class TagBasedOneTimeMetering {
  @SerializedName("key")
  private String key = null;

  @SerializedName("oneTimeMetering")
  private OneTimeMetering oneTimeMetering = null;

  @SerializedName("value")
  private String value = null;

  public TagBasedOneTimeMetering key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Get key
   *
   * @return key
   */
  @Schema(description = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public TagBasedOneTimeMetering oneTimeMetering(OneTimeMetering oneTimeMetering) {
    this.oneTimeMetering = oneTimeMetering;
    return this;
  }

  /**
   * Get oneTimeMetering
   *
   * @return oneTimeMetering
   */
  @Schema(description = "")
  public OneTimeMetering getOneTimeMetering() {
    return oneTimeMetering;
  }

  public void setOneTimeMetering(OneTimeMetering oneTimeMetering) {
    this.oneTimeMetering = oneTimeMetering;
  }

  public TagBasedOneTimeMetering value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   *
   * @return value
   */
  @Schema(description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TagBasedOneTimeMetering tagBasedOneTimeMetering = (TagBasedOneTimeMetering) o;
    return Objects.equals(this.key, tagBasedOneTimeMetering.key)
        && Objects.equals(this.oneTimeMetering, tagBasedOneTimeMetering.oneTimeMetering)
        && Objects.equals(this.value, tagBasedOneTimeMetering.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, oneTimeMetering, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TagBasedOneTimeMetering {\n");

    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    oneTimeMetering: ").append(toIndentedString(oneTimeMetering)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
