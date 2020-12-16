# Get detailed metadata for a resource action

### Step name
vraGetResourceActionDetails

### Description
Returns detailed information about a resource action

### Parameters
| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| username | String | vRealize Automation Username (mutually exclusive with ```token```)
| password | String | vRealize Automation Password (mutually exclusive with ```token```)
| domain | String | vRealize Automation authentication domain (mutually exclusive with ```token```)
| trustSelfSignedCert | Boolean | Trust self-signed certificates (not recommended) |
| deploymentId | String |The ID of the deployment to run an action against (mutually exclusive with deploymentName) |
| deploymentName | String | The name of the deployment to run an action against (mutually exclusive with deploymentId) |
| resourceName | String |The name of a resource within the blueprint. If left blank, the action is run against the top level Deployment |
| actionId | String | The type of action to run. See below for examples. |

### Return value
Returns a list of detailed resource action metadata. Since the resource name could match
multiple instance of a resource, the return value is an array,

### vRealize Automation URL and token
If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the 
global settings (if present).

### Action IDs 
The set of action IDs is open ended and depends on the types of resources deployed. 
Refer to the vRealize Automation API documentation for details. This is a [list of common 
action IDs](actionIds.md).

### Examples

#### Getting metadata for a resource action

```groovy
node {
    def ra = vraGetResourceActionDetails(
        vraURL: env.vraURL,
        token: env.token,
        deploymentId: dep[0].id,
        resourceName: 'UbuntuMachine',
        actionId: 'Cloud.AWS.EC2.Instance.PowerOff')
}
```
