# Delete a deployment

### Step name
vraDeleteDeployment

### Description
Deletes a deployment and all its resources and waits for the request to complete.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| trustSelfSignedCert | Boolean | Trust self-signed certificates (not recommended) |
| deploymentId | String |The ID of the deployment to delete (mutually exclusive with deploymentName) |
| deploymentName | String | The name of the deployment to delete (mutually exclusive with deploymentId) |
| timeout | Long | Timeout for the deletion to complete, in seconds (default: 300) |

### Return value
Returns a map corresponding to the fields of a [DeploymentRequest](https://prydin.github.io/vrealize-automation-plugin-for-jenkins/apidocs/com/vmware/vra/jenkinsplugin/model/deployment/DeploymentRequest.html)

### vRealize Automation URL and token
If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the 
global settings (if present).

### Example
```groovy
node {
    def deployment = vraDeleteDeployment(
        deploymentName: "My Deployment",
        timeout: 300)
    assert deployment != null
    assert deployment.status == "SUCCESSFUL"
}
```