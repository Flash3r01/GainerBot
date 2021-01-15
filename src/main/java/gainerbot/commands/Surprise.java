package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;

public class Surprise extends BaseCommand{
    private int fileSurpriseNum;
    private File surpriseFile;

    public Surprise() {
        super(new String[]{"surprise"}, "What is it going to do?");
        listeningChannels = IChannelPermission.Presets.getChatPermissions();
        initSurprises();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        String surprise;
        if(fileSurpriseNum != 0){
            int selectedSurprise = GainerBotConfiguration.random.nextInt(fileSurpriseNum);
            surprise = getSurpriseByNum(selectedSurprise);
        }else{
            surprise = "I dont have any surprises :(";
        }

        if(surprise == null) return;

        event.getChannel().sendMessage(surprise).queue();
    }

    private String getSurpriseByNum(int num){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(surpriseFile));

            int current = 0;
            String line;
            while((line = reader.readLine()) != null){
                if(!line.startsWith("#") && !line.isBlank()){
                    if(current >= num){
                        reader.close();
                        return line;
                    }else{
                        current++;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Counts how many non-empty and non-comment lines the surprise-file contains.
     */
    private void initSurprises(){
        File surpriseFile;

        try {
            surpriseFile = Paths.get(GainerBotConfiguration.basePath.toString(), "surprise", "surprise.txt").toFile();
        }catch (InvalidPathException pathE){
            System.out.println("Was not able to create the Surprise-File Path.");
            fileSurpriseNum = 0;
            return;
        }

        if(surpriseFile.exists()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(surpriseFile));
                String line;
                int accu = 0;
                while ((line = reader.readLine()) != null){
                    if(!line.startsWith("#") && !line.isBlank()) accu++;
                }

                if(accu != 0) this.surpriseFile = surpriseFile;
                fileSurpriseNum = accu;
                reader.close();
            } catch (IOException e) {
                fileSurpriseNum = 0;
            }
        }else{
            fileSurpriseNum = 0;
        }
    }
}
