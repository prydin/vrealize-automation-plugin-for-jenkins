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
    // Version omitted. Should pick latest.
    def dep = vraDeployFromCatalog(
            trustSelfSignedCert: true,
            catalogItemName: 'jenkins-test',
            count: 1,
            deploymentName: 'JenkinsProgrammatic-#',
            projectName: 'JenkinsTest',
            reason: 'Test',
            timeout: 300,
            inputs: '{ username: \'testuser\' }')
    assert dep != null
    def addr = vraWaitForAddress(
            trustSelfSignedCert: true,
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')

    // Make sure the deployment can be found using its name
    // (https://github.com/prydin/vrealize-automation-plugin-for-jenkins/issues/7)
    def thisDep = vraGetDeployment(
            trustSelfSignedCert: true,
            deploymentName: dep[0].name)
    assert thisDep.id == dep[0].id
    assert thisDep.name == dep[0].name

    def ras = vraGetResourceActions(
            vraURL: env.vraURL,
            token: env.token,
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')
    assert ras != null
    assert ras.size() == 1
    assert ras[0].size() > 0

    def ra = vraGetResourceActionDetails(
            vraURL: env.vraURL,
            token: env.token,
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine',
            actionId: 'Cloud.AWS.EC2.Instance.PowerOff')
    assert ra != null
    assert ra.size() == 1
    assert ra[0].name == 'PowerOff'
    assert ra[0].id == 'Cloud.AWS.EC2.Instance.PowerOff'

    echo "Deployed: $dep[0].id, addresses: ${addr[0]}"
    def dep2 = vraDeleteDeployment(
            trustSelfSignedCert: true,
            deploymentName: dep[0].name)
    assert dep2 != null
    assert dep2.id != null
    assert dep2.status == "SUCCESSFUL";
}


