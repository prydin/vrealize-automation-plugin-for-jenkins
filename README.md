# VMWare vRealize Automation Plugin for Jenkins

## Introduction

This plugin lets you create, manage and destroy virtual machines, networks, load balancers
and other resources managed by vRealize Automation 8.x. 

The plugin is currently focused on pipelines rather than freestyle project. If you see
a need for support for freestyle, please open an issue and we will put that on the 
roadmap for future development.

## Getting started

Install the plugin, either by selecting it from the plugin library in Jenkins (coming soon)
or by uploading the HPI file. 

In your application repository, create a ```Jenkinsfile``` with contents similar to this:

```groovy
node {
    // Submit a deployment request and wait for it complete
    def deployment = vraDeployFromCatalog(
            catalogItemName: '<catalog item name>',
            count: 1,
            deploymentName: 'Jenkins-#',
            projectName: '<project name>',
            reason: 'I have my reasons!',
            timeout: 300,
            version: '2',
            inputs: '{ username: \'testuser\' }')
    assert deployment != null

    // Wait for the machine to receive a valid IP address. 
    def addr = vraWaitForAddress(
            deploymentId: deployment[0].id,
            resourceName: '<name of a machine within the blueprint>')
    echo "Deployed: $deployment[0].id, addresses: ${addr}"
}
```

That's it! Create a pipeline project in Jenkins that's linked to your repository, and you 
can start using it.

You have now deployed a machine and received an IP address for it. You may 
use it for SSH steps etc.

## Global settings
This plugin allows you to configure the address of the vRealize Automation environment, as
well as its API key. This is useful when you're only interacting with a single instance
of vRealize Automation and allows you to omit address and credentials from the actual 
pipelines. The credentials are configured as a reference to a standard Jenkins string
credential.

![Global Settings](docs/img/global_settings.png)

## Available pipeline steps
* [vraDeleteDeployment - Deleta a deployment and its resources](docs/vraDeleteDeployment.md)
* [vraDeployFromCatalog - Deploy from a catalog item](docs/vraDeployFromCatalog.md)
* [vraGetDeployment - Get details about a deployment](docs/vraGetDeployment.md)
* [vraRunAction - Run a day two operation](docs/vraRunAction.md)
* [vraWaitForAddress - Wait until a resource has a valid address and return it](docs/vraWaitForAddress.md)

## Javadoc
[Javadoc](https://prydin.github.io/vrealize-automation-plugin-for-jenkins/apidocs/)

## Contributing

We strongly encourage contributions to this project and would love to include your code and ideas!
For details, please refer to [CONTRIBUTING.md](CONTRIBUTING.md)

