package org.sonar.java.checks.helpers;

import org.junit.Test;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentifierUtilsTest extends JavaParserHelper {

  @Test
  public void simpleAssignment() {
    String code = newCode( "int foo() {",
      "boolean a = true;",
      "return a;",
      "}");
    assertThatLastReassignmentsOfReturnedVariableIsEqualTo(code, true);
  }

  @Test
  public void selfAssigned() {
    String code = newCode( "int foo() {",
      "boolean a = a;",
      "return a;",
      "}");
    assertThatLastReassignmentsOfReturnedVariableIsEqualTo(code, null);
  }

  @Test
  public void unknownValue() {
    String code = newCode( "int foo(boolean a) {",
      "return a;",
      "}");
    assertThatLastReassignmentsOfReturnedVariableIsEqualTo(code, null);
  }

  @Test
  public void notAnIdentifier() {
    String code = newCode( "int foo() {",
      "boolean a = bar();",
      "return a;",
      "}",
      "boolean bar() {",
      "return true;",
      "}");
    assertThatLastReassignmentsOfReturnedVariableIsEqualTo(code, null);
  }

  private <T> void assertThatLastReassignmentsOfReturnedVariableIsEqualTo(String code, T target) {
    MethodTree method = methodTree(code);
    IdentifierTree a = variableFromLastReturnStatement(method.block().body());
    Boolean value = IdentifierUtils.getValue(a, ConstantUtils::resolveAsBooleanConstant);
    assertThat(value).isEqualTo(target);
  }
}