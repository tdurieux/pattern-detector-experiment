package com.squareup.wire.logger;

import com.squareup.wire.OutputArtifact;

public interface WireLogger {
  public void setQuiet(boolean isQuiet);
  public void error(String message);
  public void artifact(OutputArtifact artifact);
  public void info(String message);
}
