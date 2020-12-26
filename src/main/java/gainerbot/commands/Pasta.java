package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Pasta extends BaseCommand {
    private String[] pastaNames;
    private File pastaBase;

    public Pasta() {
        super(new String[]{"pasta"}, "Responds with the respective copypasta.");
        listeningChannels = IChannelPermission.Presets.getChatPermissions();
        initPasta();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        //Handle command without options
        if(options.length <= 0){
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("Following copypastas are available:").append("\n");
            for(String name : pastaNames){
                strBuilder.append(name).append("\n");
            }
            event.getChannel().sendMessage(strBuilder.toString()).queue();
            event.getMessage().delete().queue();
        }
        //Handle command with 1 option
        else if(options.length == 1){
            for(String available : pastaNames){
                if(options[0].equalsIgnoreCase(available)){
                    File pastaFile = new File(Paths.get(pastaBase.toString(), available + ".txt").toString());
                    if(pastaFile.exists()){
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(pastaFile, StandardCharsets.UTF_8));
                            StringBuilder msgBuilder = new StringBuilder();

                            String line;
                            while((line = reader.readLine()) != null){
                                msgBuilder.append(line).append("\n");
                            }
                            reader.close();

                            event.getChannel().sendMessage(msgBuilder.toString()).queue();
                            event.getMessage().delete().queue();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("The requested pasta file most likely has been modified.");
                        }
                    }
                }
            }
        }
    }

    private void initPasta(){
        pastaBase = Paths.get(GainerBotConfiguration.basePath.toString(), "pasta").toFile();
        ArrayList<String> names = new ArrayList<>();
        File[] files = pastaBase.listFiles();
        if(files == null){
            pastaNames = new String[0];
            return;
        }
        for(File file : files){
            String name = file.toPath().getFileName().toString();
            if(name.endsWith(".md")) continue;
            name = name.replace(".txt", "");
            names.add(name);
        }
        pastaNames = names.toArray(new String[0]);
    }
}
