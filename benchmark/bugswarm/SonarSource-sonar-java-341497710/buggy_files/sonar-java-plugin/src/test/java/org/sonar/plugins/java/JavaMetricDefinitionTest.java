package org.sonar.plugins.java;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaMetricDefinitionTest {

  @Test
  public void metrics_defined() {
    assertThat(new JavaMetricDefinition().getMetrics()).hasSize(1);
  }
}
