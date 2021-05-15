package gainerbot.schnitzel;

import gainerbot.GainerBotConfiguration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Manages the SchnitzelHuntInstances
 */
public class SchnitzelHuntManager {

    private final HashMap<String, SchnitzelHuntInstance> instances;


    private SchnitzelHuntManager(HashMap<String, SchnitzelHuntInstance> instances) {
        this.instances = instances;
    }

    public static SchnitzelHuntManager getSchnitzelManager(){
        Path schnitzelHuntBasePath = GainerBotConfiguration.basePath.resolve("schnitzel");
        return fromPath(schnitzelHuntBasePath);
    }

    private static SchnitzelHuntManager fromPath(Path basePath){
        HashMap<String, SchnitzelHuntInstance> instances = new HashMap<>();
        File[] folders = basePath.toFile().listFiles(File::isDirectory);
        for(File userFolder : folders){
            String id = userFolder.toPath().getFileName().toString();
            if(id.startsWith(".")) continue;
            instances.put(id, SchnitzelHuntInstance.fromBaseFile(userFolder));
        }

        return new SchnitzelHuntManager(instances);
    }

    public void handlePrivateMessage(MessageReceivedEvent event) {
        String senderId = event.getAuthor().getId();
        if(instances.containsKey(senderId)){
            instances.get(senderId).handleMessageReceived(event);
        }
    }
}
