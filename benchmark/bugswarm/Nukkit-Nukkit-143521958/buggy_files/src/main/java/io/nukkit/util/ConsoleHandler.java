package io.nukkit.util;

import io.nukkit.Nukkit;
import io.nukkit.Server;
import jline.console.ConsoleReader;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ConsoleHandler extends Thread {

    private final Server server;

    public ConsoleHandler(Server server) {
        super("Server console handler");

        this.server = server;
    }

    @Override
    public void run() {
        if (Nukkit.useConsole) {
            ConsoleReader reader = this.server.reader;

            try {
                while (!server.isStopped() && server.isRunning()) {
                    String line;

                    if (Nukkit.useJline) {
                        line = reader.readLine("> ", null);
                    } else {
                        line = reader.readLine();
                    }

                    if (line != null && !line.trim().isEmpty()) {
                        server.issueCommand(line);
                    }
                }
            } catch (IOException e) {
                Server.LOGGER.error("Exception handling console input", e);
            }
        }
    }
}
