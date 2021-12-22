package gainerbot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public enum GainerBotConfiguration {
    instance;

    // Debug Stuff
    public static boolean isDebug = false;
    public static String debugGuildId = null;

    // Command Stuff
    public static String prefix = isDebug ? "?" : "!!";


    // Paths
    public static Path basePath = Paths.get(System.getProperty("user.dir"), "data");


    public static String databaseName = "gainerbotDatabase";


    public static Random random = new Random();
}
