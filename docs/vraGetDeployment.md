# Get details of a deployment

### Step name
vraGetDeployment

### Description
Returns a snapshot of the full set of details about a deployment. 

### Parameters
| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| deploymentId | String |The ID of the deployment to fetch (mutually exclusive with deploymentName) |
| deploymentName | String | The name of the deployment to fetch (mutually exclusive with deploymentId) |
| expandResources | Boolean | If true, information about all the resources in the deployment are loaded. See remark below! |
| timeout | Long | Timeout for the deletion to complete, in seconds (default: 300) |

### Return value
Returns a map corresponding to the fields of a [Deployment](https://prydin.github.io/vrealize-automation-plugin-for-jenkins/apidocs/com/vmware/vra/jenkinsplugin/model/catalog/Deployment.html). 

### vRealize Automation URL and token
If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the 
global settings (if present).

### Remarks 
Setting the ```expandResources``` flag to ```true``` may make the step run considerably slower and
consume more server resources for very large deployments. Therefore, it is recommended to set
it to ```false``` unless full resource information is needed.

### Example
```groovy
node {
    def deployment = vraGetDeployment(
        deploymentName: "My Deployment",
        timeout: 300)
    echo "My deployment has ID ${deployment.id} and is in state ${deployment.status}"
}
```