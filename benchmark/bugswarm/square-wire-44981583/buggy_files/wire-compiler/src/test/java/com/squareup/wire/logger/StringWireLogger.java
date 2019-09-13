package com.squareup.wire.logger;

import com.squareup.wire.OutputArtifact;

/**
 * Created by zundel on 12/23/14.
 */
public class StringWireLogger implements WireLogger {
  private boolean isQuiet = false;
  private StringBuilder buffer = new StringBuilder();

  @Override
  public void setQuiet(boolean isQuiet) {
    this.isQuiet = isQuiet;
  }

  @Override public void error(String message) {
    buffer.append(message);
    buffer.append('\n');

  }

  @Override public void artifact(OutputArtifact artifact) {
    buffer.append(artifact.getArtifactFile().toString());
    buffer.append('\n');
  }

  @Override public void info(String message) {
    if (!isQuiet) {
      buffer.append(message);
      buffer.append('\n');
    }
  }

  public String getLog() {
    return buffer.toString();
  }
}
