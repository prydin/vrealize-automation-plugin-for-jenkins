# Run a day 2 operation

### Step name
Runs a day 2 operation on a deployment or resource

### Description
Returns a snapshot of the full set of details about a deployment. 

### Parameters
| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| trustSelfSignedCert | Boolean | Trust self-signed certificates (not recommended) |
| deploymentId | String |The ID of the deployment to run an action against (mutually exclusive with deploymentName) |
| deploymentName | String | The name of the deployment to run an action against (mutually exclusive with deploymentId) |
| resourceName | String |The name of a resource within the blueprint. If left blank, the action is run against the top level Deployment |
| actionId | String | The type of action to run. See below for examples. |
| reason | String | The reason for running the action |
| inputs | String or Map | The action input parameters as a JSON encoded string or a native Map |
| timeout | Long | Timeout for the deletion to complete, in seconds (default: 300) |

### Return value
Returns a list of maps corresponding to the fields of a [DeploymentRequest](https://prydin.github.io/vrealize-automation-plugin-for-jenkins/apidocs/com/vmware/vra/jenkinsplugin/model/deployment/DeploymentRequest.html).

### vRealize Automation URL and token
If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the 
global settings (if present).

### Action IDs 
The set of action IDs is open ended and depends on the types of resources deployed. 
Refer to the vRealize Automation API documentation for details. This is a [list of common 
action IDs](actionIds.md).



### Remarks 
Setting the ```expandResources``` flag to ```true``` may make the step run considerably slower and
consume more server resources for very large deployments. Therefore, it is recommended to set
it to ```false``` unless full resource information is needed.

### Examples

#### Powering down a single machine

```groovy
node {
    // Power off a machine
    def rq = vraRunAction(
            deploymentName: 'MyDeployment',
            resourceName: 'UbuntuMachine',
            actionId: 'Cloud.AWS.EC2.Instance.PowerOff',
            reason: 'Because I can',
            timeout: 300
    )
    println "Powering down the machine was ${rq[0].status}"
}
```
#### Powering down all resources of a deployment

```groovy
node {
    // Power off a machine
    def rq = vraRunAction(
            deploymentName: 'MyDeployment',
            actionId: 'Deployment.PowerOff',
            reason: 'Because I can',
            timeout: 300
    )
    println "Powering down the deployment was ${rq[0].status}"
}
```
