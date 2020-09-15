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
    def addr = vraWaitForAddress(
            deploymentId: dep[0].id,
            resourceName: 'UbuntuMachine')
    echo "Deployed: $dep[0].id, addresses: ${addr[0]}"
    def dep2 = vraDeleteDeployment(deploymentName: dep[0].name)
    assert dep2 != null
    assert dep2.id != null
    assert dep2.status == "SUCCESSFUL";
}


