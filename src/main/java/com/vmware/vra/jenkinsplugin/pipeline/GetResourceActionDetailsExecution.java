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

package com.vmware.vra.jenkinsplugin.pipeline;

import com.vmware.vra.jenkinsplugin.util.MapUtils;
import com.vmware.vra.jenkinsplugin.vra.VraApi;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

public class GetResourceActionDetailsExecution extends SynchronousNonBlockingStepExecution<Object> {

  private static final long serialVersionUID = -367137360179308720L;
  private final GetResourceActionDetailsStep step;

  public GetResourceActionDetailsExecution(
      final StepContext context, final GetResourceActionDetailsStep step) {
    super(context);
    this.step = step;
  }

  @Override
  protected Object run() throws Exception {
    final VraApi client = step.getClient();
    step.validate();
    final String depId = step.resolveDeploymentId();
    final List<UUID> resIds = step.resolveResourceIds();
    final List<Object> result = new ArrayList<>(resIds.size());
    for (final UUID id : resIds) {
      result.add(client.getResourceActionDetails(depId, id.toString(), step.getActionId()));
    }
    return MapUtils.mappify(result);
  }
}
