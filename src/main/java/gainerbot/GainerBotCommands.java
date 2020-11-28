package gainerbot;

import gainerbot.commands.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        commands.add(new Test());
        commands.add(new Google());
        commands.add(new Watch2Gether());
        commands.add(new Nibba());
        commands.add(new Fuck());

        //Extract all the commandNames.
        for(BaseCommand command : commands){
            commandNames.add(command.names);
        }
    }


    public boolean processCommandMessage(@Nonnull MessageReceivedEvent event){
        String msg = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        if(!msg.startsWith(prefix)) return false;

        String[] tokens = commandToTokens(msg);
        if(tokens.length == 0) return false;

        String commandString = tokens[0];
        int currentCommandIndex = 0;
        for(String[] names : commandNames){
            for(String name : names){
                if(commandString.equalsIgnoreCase(name)){
                    BaseCommand command = commands.get(currentCommandIndex);

                    if(command.listensOnChannel(channel.getName())) {
                        //Choose if there are options or not.
                        if (tokens.length >= 2) {
                            command.execute(event, Arrays.copyOfRange(tokens, 1, tokens.length));
                        } else {
                            command.execute(event, new String[0]);
                        }
                        return true;
                    }else{
                        return false;
                    }

                }
            }
            currentCommandIndex++;
        }

        return false;
    }

    private String[] commandToTokens(String msg){
        String[] quoteSeparated = msg.substring(prefix.length()).strip().split("\"");

        ArrayList<String> tokens = new ArrayList<>();
        for(int i = 0; i < quoteSeparated.length; i++){
            if(i%2 == 0){
                Collections.addAll(tokens, quoteSeparated[i].strip().split(" "));
            }else{
                Collections.addAll(tokens, quoteSeparated[i]);
            }
        }

        return tokens.toArray(new String[0]);
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
