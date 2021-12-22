package gainerbot.slashCommand.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Pasta extends BaseSlashCommand {
    private String[] pastaNames;
    private File pastaBase;

    public Pasta() {
        super(new CommandData("pasta", "Sends a copypasta")
                .addOption(OptionType.STRING, "name", "Name of the pasta", false));
        initPasta();
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        OptionMapping NameOption = event.getOption("name");
        String name = NameOption != null ? NameOption.getAsString() : null;

        // Handle no option given
        if (name == null){
            event.deferReply(true).queue();
            StringBuilder strBuilder = new StringBuilder();

            strBuilder.append("Following copypastas are available:").append("\n");
            for(String currentName : pastaNames){
                strBuilder.append(currentName).append("\n");
            }

            event.getHook().sendMessage(strBuilder.toString()).queue();
        }
        // Handle option given
        else{
            String pastaFileName = getPastaFileName(name);
            if (pastaFileName == null) {
                event.reply("Mamma mia!\nYour pasta \""+name+"\" does not exist!").setEphemeral(true).queue();
                return;
            }

            File pastaFile = new File(Paths.get(pastaBase.toString(), pastaFileName).toString());
            if (!pastaFile.exists()){
                event.reply("Mamma mia!\nI seem to have misplaced your beloved \""+name+"\" pasta!").setEphemeral(true).queue();
                return;
            }

            event.deferReply().queue();
            try (BufferedReader reader = new BufferedReader(new FileReader(pastaFile, StandardCharsets.UTF_8))) {

                String line = reader.readLine();
                if (line == null){
                    event.getHook().sendMessage("This pasta is currently not available :/").queue();
                    return;
                }
                event.getHook().sendMessage(line).queue();

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    event.getChannel().sendMessage(line).queue();
                }

            } catch (IOException e) {
                e.printStackTrace();
                event.getHook().sendMessage("Mamma mia!\nI encountered an error while cooking the \""+name+"\" pasta!").queue();
            }
        }
    }

    private String getPastaFileName(String pastaName){
        for (String currentName : pastaNames){
            if (currentName.equalsIgnoreCase(pastaName)) return currentName + ".txt";
        }
        return null;
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
            if(!name.endsWith(".txt")) continue;
            name = name.replace(".txt", "");
            names.add(name);
        }
        pastaNames = names.toArray(new String[0]);
    }
}
