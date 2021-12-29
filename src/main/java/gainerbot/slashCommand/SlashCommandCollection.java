package gainerbot.slashCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashCommandCollection {
    private final Map<String, BaseSlashCommand> commands = new HashMap<>();

    public void addCommand(BaseSlashCommand command){
        if (commands.containsKey(command.getCommandData().getName())){
            System.out.println("INFO: Overwriting command \""+command.getCommandData().getName()+"\" in "+this+".");
        }
        commands.put(command.getCommandData().getName(), command);
    }

    public Map<String, BaseSlashCommand> getCommands() {
        return commands;
    }

    public List<BaseSlashCommand> getCommandsAsList() {
        return new ArrayList<>(commands.values());
    }
}
