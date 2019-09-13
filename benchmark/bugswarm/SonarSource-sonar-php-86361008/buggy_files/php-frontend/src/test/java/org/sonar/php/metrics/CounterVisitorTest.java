package org.sonar.php.metrics;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class CounterVisitorTest extends MetricTest {

  @Test
  public void test_class() throws Exception {
    CounterVisitor counterVisitor = new CounterVisitor(parse("classes.php"));
    assertThat(counterVisitor.getClassNumber()).isEqualTo(4);
  }

  @Test
  public void test_statements() throws Exception {
    CounterVisitor counterVisitor = new CounterVisitor(parse("statements.php"));
    assertThat(counterVisitor.getStatementNumber()).isEqualTo(29);
  }

  @Test
  public void test_functions() throws Exception {
    CounterVisitor counterVisitor = new CounterVisitor(parse("functions.php"));
    assertThat(counterVisitor.getFunctionNumber()).isEqualTo(4);
  }

}
