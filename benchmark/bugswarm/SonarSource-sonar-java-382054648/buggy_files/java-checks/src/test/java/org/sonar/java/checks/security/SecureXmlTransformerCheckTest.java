package org.sonar.java.checks.security;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SecureXmlTransformerCheckTest {

  @Test
  public void test() {
    JavaCheckVerifier.verify("src/test/files/checks/security/SecureXmlTransformerCheck.java", new SecureXmlTransformerCheck());
  }

}
