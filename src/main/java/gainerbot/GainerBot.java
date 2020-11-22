package gainerbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GainerBot extends ListenerAdapter {
    public static JDA jdaInstance;

    public static GainerBotCommands commandManager = new GainerBotCommands();


    public static void startGainerBot(){
        if(jdaInstance != null){
            //TODO add an error message
            return;
        }

        //TODO read all the configuration at once.
        String token = "";
        try {
            Path path = Paths.get(GainerBotConfiguration.basePath.toString(), "token.txt");
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            token = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find the Token-File.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read the Token from the File.");
        }

        //Create the Bot Instance
        //TODO make it read the token from a file.
        JDABuilder builder = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("Schnapp - Gzuz"))
                .disableCache(CacheFlag.ACTIVITY)
                .addEventListeners(new GainerBot());
                //.setCompression(Compression.NONE);

        //TODO add an actual logger.
        try {
            jdaInstance = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.out.println("Could not login.");
        }
        try {
            jdaInstance.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("A needed Thread was interrupted.");
        }
        System.out.println("GainerBot started.");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        MessageChannel channel = event.getChannel();

        //TODO Remove these Test-Commands
        if(channel.getName().toLowerCase().contains("bot")) {
            if (msg.contains("!test")) {
                channel.sendMessage("Leave me alone, you KEK!").queue();
            } else if (msg.contains("!google")) {
                String url = "https://lmgtfy.app/?q=" + msg.replace("!google", "").strip().replace("+", "%2B").replace(" ", "+");
                channel.sendMessage(url).queue();
            }

            commandManager.processCommandMessage(event);
        }
    }
}
