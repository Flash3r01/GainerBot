package gainerbot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public enum GainerBotConfiguration {
    instance;

    //Command Stuff
    public static String prefix = "!!";


    //Paths
    public static Path basePath = Paths.get(System.getProperty("user.dir"), "data");


    public static Random random = new Random();
}
