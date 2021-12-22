package gainerbot.slashCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This class restricts the registering of commands to disallow two commands with the same name. Even on two separate guilds.
// This class does not care about currently set commands. It even registers the commands with discord, if there are no changes.
public class SlashCommandManager {
    private static final Set<String> registeredNames = new HashSet<>();
    private final Map<String, BaseSlashCommand> commandMap = new HashMap<>();
    private SlashCommandManagerState state = new CollectingState(this);


    public boolean addCommand(BaseSlashCommand toAdd){
        return state.addCommand(toAdd);
    }

    public boolean registerGlobalCommands(JDA jdaInstance){
        return state.registerGlobalCommands(jdaInstance);
    }

    public boolean registerGuildCommands(Guild guild){
        return state.registerGuildCommands(guild);
    }


    //#region Getter/Setter
    public Map<String, BaseSlashCommand> getCommandMap() {
        return commandMap;
    }

    public static Set<String> getRegisteredNames() {
        return registeredNames;
    }

    public void setState(SlashCommandManagerState state) {
        this.state = state;
    }
    //endregion
}
