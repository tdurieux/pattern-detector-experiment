package com.squareup.wire;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OutputArtifactTest {

  @Test
  public void testOutputArtifact() {

    OutputArtifact artifact = new OutputArtifact("foo/bar", "com.company", "Foo");
    assertEquals("foo/bar", artifact.getOutputDirectory());
    assertEquals("com.company", artifact.getJavaPackage());
    assertEquals("Foo", artifact.getClassName());

    assertEquals("foo/bar/com/company", artifact.getArtifactDir().toString());
    assertEquals("foo/bar/com/company/Foo.java", artifact.getArtifactFile().toString());
    assertEquals("com.company.Foo", artifact.getFullClassName());
  }
}
