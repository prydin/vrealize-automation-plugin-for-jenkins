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
 * VMware Cloud Assembly IaaS API
 * A multi-cloud IaaS API for Cloud Automation Services
 *
 * OpenAPI spec version: 2019-01-15
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.vmware.vra.jenkinsplugin.model.iaas;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * State object representing an Nsx-V cloud account.&lt;br&gt;&lt;br&gt;A cloud account identifies a
 * cloud account type and an account-specific deployment region or data center where the associated
 * cloud account resources are hosted.&lt;br&gt;**HATEOAS** links:&lt;br&gt;**self** -
 * CloudAccountNsxV - Self link to this cloud account
 */
@Schema(
    description =
        "State object representing an Nsx-V cloud account.<br><br>A cloud account identifies a cloud account type and an account-specific deployment region or data center where the associated cloud account resources are hosted.<br>**HATEOAS** links:<br>**self** - CloudAccountNsxV - Self link to this cloud account")
@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
    date = "2020-09-09T18:26:35.661905-04:00[America/New_York]")
public class CloudAccountNsxV {
  @SerializedName("owner")
  private String owner = null;

  @SerializedName("hostName")
  private String hostName = null;

  @SerializedName("_links")
  private Map<String, Href> _links = new HashMap<String, Href>();

  @SerializedName("description")
  private String description = null;

  @SerializedName("orgId")
  private String orgId = null;

  @SerializedName("tags")
  private List<Tag> tags = null;

  @SerializedName("organizationId")
  private String organizationId = null;

  @SerializedName("createdAt")
  private String createdAt = null;

  @SerializedName("customProperties")
  private Map<String, String> customProperties = null;

  @SerializedName("dcid")
  private String dcid = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("id")
  private String id = null;

  @SerializedName("updatedAt")
  private String updatedAt = null;

  @SerializedName("username")
  private String username = null;

  public CloudAccountNsxV owner(String owner) {
    this.owner = owner;
    return this;
  }

  /**
   * Email of the user that owns the entity.
   *
   * @return owner
   */
  @Schema(example = "csp@vmware.com", description = "Email of the user that owns the entity.")
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public CloudAccountNsxV hostName(String hostName) {
    this.hostName = hostName;
    return this;
  }

  /**
   * Host name for the Nsx-V cloud account
   *
   * @return hostName
   */
  @Schema(
      example = "nsxv.vmware.com",
      required = true,
      description = "Host name for the Nsx-V cloud account")
  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public CloudAccountNsxV _links(Map<String, Href> _links) {
    this._links = _links;
    return this;
  }

  public CloudAccountNsxV putLinksItem(String key, Href _linksItem) {
    this._links.put(key, _linksItem);
    return this;
  }

  /**
   * HATEOAS of the entity
   *
   * @return _links
   */
  @Schema(required = true, description = "HATEOAS of the entity")
  public Map<String, Href> getLinks() {
    return _links;
  }

  public void setLinks(Map<String, Href> _links) {
    this._links = _links;
  }

  public CloudAccountNsxV description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A human-friendly description.
   *
   * @return description
   */
  @Schema(example = "my-description", description = "A human-friendly description.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CloudAccountNsxV orgId(String orgId) {
    this.orgId = orgId;
    return this;
  }

  /**
   * The id of the organization this entity belongs to.
   *
   * @return orgId
   */
  @Schema(example = "9.0E+49", description = "The id of the organization this entity belongs to.")
  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public CloudAccountNsxV tags(List<Tag> tags) {
    this.tags = tags;
    return this;
  }

  public CloudAccountNsxV addTagsItem(Tag tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<Tag>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * A set of tag keys and optional values that were set on the Cloud Account
   *
   * @return tags
   */
  @Schema(
      example = "[ { \"key\" : \"env\", \"value\": \"dev\" } ]",
      description = "A set of tag keys and optional values that were set on the Cloud Account")
  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public CloudAccountNsxV organizationId(String organizationId) {
    this.organizationId = organizationId;
    return this;
  }

  /**
   * This field is deprecated. Use orgId instead. The id of the organization this entity belongs to.
   *
   * @return organizationId
   */
  @Schema(
      example = "deprecated",
      description =
          "This field is deprecated. Use orgId instead. The id of the organization this entity belongs to.")
  public String getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  public CloudAccountNsxV createdAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Date when the entity was created. The date is in ISO 8601 and UTC.
   *
   * @return createdAt
   */
  @Schema(
      example = "2012-09-27",
      description = "Date when the entity was created. The date is in ISO 8601 and UTC.")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public CloudAccountNsxV customProperties(Map<String, String> customProperties) {
    this.customProperties = customProperties;
    return this;
  }

  public CloudAccountNsxV putCustomPropertiesItem(String key, String customPropertiesItem) {
    if (this.customProperties == null) {
      this.customProperties = new HashMap<String, String>();
    }
    this.customProperties.put(key, customPropertiesItem);
    return this;
  }

  /**
   * Additional properties that may be used to extend the base type.
   *
   * @return customProperties
   */
  @Schema(
      example = "{ \"isExternal\" : \"false\" }",
      description = "Additional properties that may be used to extend the base type.")
  public Map<String, String> getCustomProperties() {
    return customProperties;
  }

  public void setCustomProperties(Map<String, String> customProperties) {
    this.customProperties = customProperties;
  }

  public CloudAccountNsxV dcid(String dcid) {
    this.dcid = dcid;
    return this;
  }

  /**
   * Identifier of a data collector vm deployed in the on premise infrastructure.
   *
   * @return dcid
   */
  @Schema(
      example = "23959a1e-18bc-4f0c-ac49-b5aeb4b6eef4",
      description = "Identifier of a data collector vm deployed in the on premise infrastructure.")
  public String getDcid() {
    return dcid;
  }

  public void setDcid(String dcid) {
    this.dcid = dcid;
  }

  public CloudAccountNsxV name(String name) {
    this.name = name;
    return this;
  }

  /**
   * A human-friendly name used as an identifier in APIs that support this option.
   *
   * @return name
   */
  @Schema(
      example = "my-name",
      description = "A human-friendly name used as an identifier in APIs that support this option.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CloudAccountNsxV id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The id of this resource instance
   *
   * @return id
   */
  @Schema(example = "9.0E+49", required = true, description = "The id of this resource instance")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CloudAccountNsxV updatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Date when the entity was last updated. The date is ISO 8601 and UTC.
   *
   * @return updatedAt
   */
  @Schema(
      example = "2012-09-27",
      description = "Date when the entity was last updated. The date is ISO 8601 and UTC.")
  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public CloudAccountNsxV username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Username to authenticate with the cloud account
   *
   * @return username
   */
  @Schema(
      example = "administrator@mycompany.com",
      required = true,
      description = "Username to authenticate with the cloud account")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CloudAccountNsxV cloudAccountNsxV = (CloudAccountNsxV) o;
    return Objects.equals(this.owner, cloudAccountNsxV.owner)
        && Objects.equals(this.hostName, cloudAccountNsxV.hostName)
        && Objects.equals(this._links, cloudAccountNsxV._links)
        && Objects.equals(this.description, cloudAccountNsxV.description)
        && Objects.equals(this.orgId, cloudAccountNsxV.orgId)
        && Objects.equals(this.tags, cloudAccountNsxV.tags)
        && Objects.equals(this.organizationId, cloudAccountNsxV.organizationId)
        && Objects.equals(this.createdAt, cloudAccountNsxV.createdAt)
        && Objects.equals(this.customProperties, cloudAccountNsxV.customProperties)
        && Objects.equals(this.dcid, cloudAccountNsxV.dcid)
        && Objects.equals(this.name, cloudAccountNsxV.name)
        && Objects.equals(this.id, cloudAccountNsxV.id)
        && Objects.equals(this.updatedAt, cloudAccountNsxV.updatedAt)
        && Objects.equals(this.username, cloudAccountNsxV.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        owner,
        hostName,
        _links,
        description,
        orgId,
        tags,
        organizationId,
        createdAt,
        customProperties,
        dcid,
        name,
        id,
        updatedAt,
        username);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CloudAccountNsxV {\n");

    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    hostName: ").append(toIndentedString(hostName)).append("\n");
    sb.append("    _links: ").append(toIndentedString(_links)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    orgId: ").append(toIndentedString(orgId)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    organizationId: ").append(toIndentedString(organizationId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    customProperties: ").append(toIndentedString(customProperties)).append("\n");
    sb.append("    dcid: ").append(toIndentedString(dcid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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
