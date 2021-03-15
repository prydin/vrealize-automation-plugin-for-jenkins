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

package com.vmware.vra.jenkinsplugin.pipelines

node {
    def config = """
"catalogItemName": "jenkins-test"
"version": "6"
"projectName": "JenkinsTest"
"deploymentName": "JenkinsFromYaml-#"
"inputs": 
    "username": "\$USERNAME"
"count": 1
"""
    env.USERNAME = "test"
    def dep = vraDeployFromCatalog(
            configFormat: "yaml",
            config: config)
    assert dep != null
    def addr = vraWaitForAddress(
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')
    echo "Deployed: $dep[0].id, addresses: $addr"

    // Power off the machine
    def dep2 = vraRunAction(
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine',
            actionId: 'Cloud.AWS.EC2.Instance.PowerOff',
            reason: 'Because I can',
            timeout: 300
    )
    println "Power down machine: ${dep2}"
    assert dep2 != null
    assert dep2.size() == 1
    assert dep2[0].id != null
    assert dep2[0].status == 'SUCCESSFUL'

    // Power on the entire deployment
    dep2 = vraRunAction(
            deploymentId: dep[0].id,
            actionId: 'Deployment.PowerOn',
            reason: 'Because I can',
            timeout: 300
    )
    println "Power up deployment: ${dep2}"
    println dep2
    assert dep2 != null
    assert dep2.size() == 1
    assert dep2[0].id != null
    assert dep2[0].status == 'SUCCESSFUL'

    // Delete the deployment
    def dep3 = vraDeleteDeployment(deploymentName: dep[0].name)
    assert dep3 != null
    assert dep3.id != null
    assert dep3.status == "SUCCESSFUL";
}


