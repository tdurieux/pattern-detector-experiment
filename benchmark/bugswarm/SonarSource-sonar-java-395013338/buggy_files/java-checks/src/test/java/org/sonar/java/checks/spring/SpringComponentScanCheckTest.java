package org.sonar.java.checks.spring;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SpringComponentScanCheckTest {

  @Test
  public void test() {
    JavaCheckVerifier.verify("src/test/files/checks/spring/SpringComponentScanCheck.java", new SpringComponentScanCheck());
    JavaCheckVerifier.verifyNoIssueWithoutSemantic("src/test/files/checks/spring/SpringComponentScanCheck.java", new SpringComponentScanCheck());
  }

}
