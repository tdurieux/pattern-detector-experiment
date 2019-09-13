package io.nukkit;

import io.nukkit.plugin.PluginManager;
import io.nukkit.util.*;
import jline.UnsupportedTerminal;
import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Server implements Runnable {

    private final String serverVersion;
    private final String nukkitVersion = Versioning.getNukkitVersion();
    private final Logger logger = Logger.getLogger("Minecraft");

    public OptionSet options;

    public ConsoleReader reader;

    private boolean isRunning = true;
    private boolean isStopped;

    public int port = -1;

    List<String> commandQueue = new ArrayList<>();

    public Server(OptionSet options) {
        this.options = options;

        if (System.console() == null && System.getProperty(jline.TerminalFactory.JLINE_TERMINAL) == null) {
            System.setProperty(jline.TerminalFactory.JLINE_TERMINAL, UnsupportedTerminal.class.getName());

            Nukkit.useJline = false;
        }

        if (options.has("port")) {
            int port = (Integer) options.valueOf("port");
            if (port > 0) {
                this.setPort(port);
            }
        }

        this.serverVersion = Server.class.getPackage().getImplementationVersion();

        try {
            this.reader = new ConsoleReader(System.in, System.out);
            this.reader.setExpandEvents(false);
        } catch (Throwable e) {
            try {
                System.setProperty(jline.TerminalFactory.JLINE_TERMINAL, UnsupportedTerminal.class.getName());
                System.setProperty("user.language", "en");
                Nukkit.useJline = false;

                this.reader = new ConsoleReader(System.in, System.out);
                this.reader.setExpandEvents(false);
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, null, ex);
            }
        }

        Runtime.getRuntime().addShutdownHook(new ServerShutdownThread(this));

        (new ConsoleHandler(this)).start();

        (new ConsoleWriter(System.out, this.reader)).start();

        org.apache.logging.log4j.Logger rootLogger = LogManager.getRootLogger();
        System.setOut(new PrintStream(new LoggerOutputStream(rootLogger, org.apache.logging.log4j.Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(rootLogger, org.apache.logging.log4j.Level.WARN), true));
        (new Thread(this, "Server Thread")).start();
    }

    @Override
    public void run() {
        while (this.isRunning) {
            while (!commandQueue.isEmpty()) {
                String command = commandQueue.get(0);
                commandQueue.remove(0);
                dispatchCommand(command);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {

    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public String getName() {
        return "Nukkit";
    }

    public String getVersion() {
        return this.serverVersion + " (MCPE: " + Nukkit.MINECRAFT_VERSION + ")";
    }

    public String getNukkitVersion() {
        return nukkitVersion;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public PluginManager getPluginManager() {
        return null;
        //TODO:
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void issueCommand(String command) {
        this.commandQueue.add(command);
    }

    public void dispatchCommand(String command) {
        this.getLogger().info(command);
    }
}
