package io.nukkit.util;

import io.nukkit.Nukkit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Versioning {
    public static String getNukkitVersion() {
        String result = "Unknown-Version";
        InputStream stream = Nukkit.class.getClassLoader().getResourceAsStream("META-INF/maven/io.nukkit/nukkit/pom.properties");
        Properties properties = new Properties();
        if (stream != null) {
            try {
                properties.load(stream);
                result = properties.getProperty("version");
            } catch (IOException var4) {
                Logger.getLogger(Versioning.class.getName()).log(Level.SEVERE, "Could not get Nukkit version!", var4);
            }
        }

        return result;
    }
}
