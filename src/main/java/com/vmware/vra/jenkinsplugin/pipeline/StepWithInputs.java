package com.vmware.vra.jenkinsplugin.pipeline;

import java.util.Map;

public interface StepWithInputs {
  String getInputs();

  Map<String, Object> getInputMap();

  Map<String, Object> resolveInputs();
}
