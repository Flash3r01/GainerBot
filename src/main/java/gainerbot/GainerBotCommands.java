package gainerbot;

import gainerbot.commands.BaseCommand;
import gainerbot.commands.Help;
import gainerbot.commands.Random;
import gainerbot.commands.Stonks;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GainerBotCommands {
    private String prefix = GainerBotConfiguration.prefix;

    private final List<BaseCommand> commands = new ArrayList<>();
    private final List<String[]> commandNames = new ArrayList<>();

    public GainerBotCommands(){
        //Here come all the commands that the Bot knows
        commands.add(new Random());
        commands.add(new Stonks());
        commands.add(new Help());

        //Extract all the commandNames.
        for(BaseCommand command : commands){
            commandNames.add(command.names);
        }
    }


    public boolean processCommandMessage(@Nonnull MessageReceivedEvent event){
        String msg = event.getMessage().getContentRaw();

        if(!msg.startsWith(prefix)) return false;

        //TODO make it possible to use " to allow spaces.
        String[] tokens = msg.substring(prefix.length()).strip().split(" ");
        if(tokens.length == 0) return false;

        String command = tokens[0];
        int currentCommandIndex = 0;
        for(String[] names : commandNames){
            for(String name : names){
                if(command.equalsIgnoreCase(name)){
                    //Choose if there are options or not.
                    if(tokens.length >= 2){
                        commands.get(currentCommandIndex).execute(event, Arrays.copyOfRange(tokens, 1, tokens.length));
                    }else{
                        commands.get(currentCommandIndex).execute(event, new String[0]);
                    }
                    return true;
                }
            }
            currentCommandIndex++;
        }

        return false;
    }


    //region Getter and Setter
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<BaseCommand> getCommands() {
        return commands;
    }

    public List<String[]> getCommandNames() {
        return commandNames;
    }
    //endregion
}
