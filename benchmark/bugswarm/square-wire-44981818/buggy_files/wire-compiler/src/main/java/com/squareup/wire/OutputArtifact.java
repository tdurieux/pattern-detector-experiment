package com.squareup.wire;

import java.io.File;

/**
 * A container class that represents an artifact output from the compiler.
 */
public class OutputArtifact {
  private final String outputDirectory;
  private final String className;
  private final String javaPackage;
  private final File artifactFile;

  public OutputArtifact(String outputDirectory, String javaPackage, String className) {
    this.outputDirectory = outputDirectory;
    this.className = className;
    this.javaPackage = javaPackage;
    String dir = outputDirectory + File.separator
        + javaPackage.replace(".", File.separator);
    artifactFile = new File(dir, className + ".java");
  }

  public String getOutputDirectory() {
    return outputDirectory;
  }

  public String getClassName() {
    return className;
  }

  public String getJavaPackage() {
    return javaPackage;
  }

  public File getArtifactFile() {
    return artifactFile;
  }

  public File getArtifactDir() {
    return artifactFile.getParentFile();
  }

  public String getFullClassName() { return javaPackage + "." + className; }
}
