package gainerbot.slashCommand.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Surprise extends BaseSlashCommand {
    private int fileSurpriseNum;
    private File surpriseFile;

    public Surprise() {
        super(new CommandData("surprise", "What is it going to do?"));
        initSurprises();
    }


    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply(false).queue();

        String surprise;
        if(fileSurpriseNum != 0){
            int selectedSurprise = GainerBotConfiguration.random.nextInt(fileSurpriseNum);
            surprise = getSurpriseByNum(selectedSurprise);
            if(surprise == null) return;
        }else{
            surprise = "I dont have any surprises :(";
        }

        event.getHook().sendMessage(surprise).queue();
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
