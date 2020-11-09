# Return the address of a resource once it's available

### Step name
vraWaitForAddress

### Description
Returns the address (typically IPV4 or IPV6) or a resource or group of resources. Even
if the resource has been successfully deployed, there may be a delay in obtaining the
address, due to DHCP and similar mechanisms.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| vraUrl | String | URL to the vRealize Automation Instance (optional) |
| token | String | vRealize Automation API token |
| username | String | vRealize Automation Username (mutually exclusive with ```token```)
| password | String | vRealize Automation Password (mutually exclusive with ```token```)
| trustSelfSignedCert | Boolean | Trust self-signed certificates (not recommended) |
| deploymentId | String |The ID of the deployment to fetch (mutually exclusive with deploymentName) |
| deploymentName | String | The name of the deployment to fetch (mutually exclusive with deploymentId) |
| resourceName | String | The name of the resource within the blueprint to obtain an address for |
| timeout | Long | Timeout for the deletion to complete, in seconds (default: 300) |

### Return value
Returns a list of strings holding the addresses. If a resource is defined as a cluster with
more than one member, then each instance will be represented by an address in the list. The 
call won't return until addresses have been obtained for all members of a cluster.

### vRealize Automation URL and token
If the ```vraURL``` or ```token``` parameters are not specified, they are obtained from the 
global settings (if present).

### Example
```groovy
node {
    def addresses = vraWaitForAddress(
        deploymentName: "My Deployment",
        resource: "ThatOneMachine",
        timeout: 300)
    echo "The addresses of my machines are ${addresses}"
}
```
