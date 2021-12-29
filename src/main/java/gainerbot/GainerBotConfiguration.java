package gainerbot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public enum GainerBotConfiguration {
    instance;

    // Debug Stuff
    public static final boolean isDebug = false;
    public static final String debugGuildId = null;

    // Command Stuff
    public static String prefix = isDebug ? "?" : "!!";


    // Paths
    public static final Path basePath = Paths.get(System.getProperty("user.dir"), "data");


    public static final String databaseName = "gainerbotDatabase";


    public static final Random random = new Random();
}
