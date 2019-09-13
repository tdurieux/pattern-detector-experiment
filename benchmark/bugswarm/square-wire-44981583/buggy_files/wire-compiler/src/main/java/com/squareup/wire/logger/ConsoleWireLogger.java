package com.squareup.wire.logger;

import com.squareup.wire.OutputArtifact;

/**
 * Created by zundel on 12/23/14.
 */
public class ConsoleWireLogger implements WireLogger {
  private boolean isQuiet = false;

  public void setQuiet(boolean isQuiet) {
    this.isQuiet = isQuiet;
  }

  public void info(String message) {
    if (!isQuiet) {
      System.out.println(message);
    }
  }

  public void artifact(OutputArtifact artifact) {
    String msg;
    if (isQuiet) {
      msg = artifact.getArtifactFile().toString();
    } else {
      msg = "Writing generated code to " + artifact.getArtifactFile().toString();
    }
    System.out.println(msg);
  }

  public void error(String message) {
    System.err.println(message);
  }
}
