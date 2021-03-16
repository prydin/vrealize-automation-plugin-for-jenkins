# Deploy from catalog

### Step name

vraDeployFromCatalog

### Description

Creates a new deployment based on a catalog item and ties it to a specified project. This step can be run in two modes:
Either the details about the deployment are specified as individual parameters, or they are supplied as a JSON or Yaml
string. The latter is useful when you want to read the deployment specification from a file.

### Parameters

| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| username | String | vRealize Automation Username (mutually exclusive with ```token```)
| password | String | vRealize Automation Password (mutually exclusive with ```token```)
| domain | String | vRealize Automation authentication domain (mutually exclusive with ```token```)
| trustSelfSignedCert | Boolean | Trust self-signed certificates (not recommended) |
| projectName | String | The name of the associated vRealize Automation project |
| catalogItemName | String | The name of the catalog item to deploy |
| version | String | The version of the catalog item to deploy. If omitted, the latest version will be deployed |
| deploymentName | String | The name of the deployment. (Optional. Generated if omitted) |
| reason | String | The reason for the deployment. (Optional)
| inputs | String or Map | Blueprint inputs. Key-value pairs encoded as a JSON string | 
| count | Integer | Then number of copies of this catalog item to deploy |
| config | String | The entire configuration of a catalog item as a JSON or Yaml string. Environment variable substitution is supported. Mutually exculsive with ```projectName```, ```catalogItemName```, ```deploymentName```, ```reason```, ```count``` and ```inputs```.
| configFormat | String | The format of the config string. Allowed values are "yaml" or "json" |
| timeout | Long | Timeout for the deletion to complete, in seconds (default: 300) |

### Return value

Returns a list of maps corresponding to the fields of
a [Deployment](https://prydin.github.io/vrealize-automation-plugin-for-jenkins/apidocs/com/vmware/vra/jenkinsplugin/model/catalog/Deployment.html)
. The size of the array should match the supplied ```count``` parameter.

### Deployment naming

Deployment names must be unique. When you omit the deployment name, a unique name is automatically generated. If you
want a custom name and want to make sure it's unique, simply include a '#' character. This will be replaced with a UUID
once the deployment is submitted. For example "MyDeployment-#" will expand to something similar to "
MyDeployment-8F02D710-9312-4414-9B38-1BB315D8301D".

### vRealize Automation URL and token

If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the global settings (if present)
.

### Examples

#### Parameter-based

```groovy
node {
    def dep = vraDeployFromCatalog(
            catalogItemName: 'jenkins-test',
            count: 1,
            deploymentName: 'JenkinsProgrammatic-#',
            projectName: 'JenkinsTest',
            reason: 'Test',
            timeout: 300,
            version: '2',
            inputs: '{ username: \'testuser\' }')
    assert dep != null

    // Get the address
    def addr = vraWaitForAddress(
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')
    echo "Deployed: $dep[0].id, addresses: ${addr[0]}"
}
```

#### Config-file based

```groovy
node {
    def dep = vraDeployFromCatalog(
            configFormat: "yaml",
            config: readFile("infrastructure.yaml"))
    assert dep != null

    // Get the address
    def addr = vraWaitForAddress(
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')
    echo "Deployed: $dep[0].id, addresses: $addr"
}
```

infrastructure.yaml

```yaml
"catalogItemName": "jenkins-test"
"version": "2"
"projectName": "JenkinsTest"
"deploymentName": "JenkinsFromYaml-#"
"inputs":
  "username": "test"
"count": 1
```
