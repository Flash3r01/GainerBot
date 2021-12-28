package gainerbot.slashCommand;

import gainerbot.GainerBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

// This class restricts the registering of commands to disallow two commands with the same name. Even on two separate guilds.
// This class does not care about currently set commands. It even registers the commands with discord, if there are no changes.
public class SlashCommandManager {
    private static SlashCommandCollection globalCommands;
    private static final Map<String, SlashCommandCollection> guildCommands = new HashMap<>();


    public static void registerGlobalCommands(JDA jdaInstance, SlashCommandCollection commands){
        if (globalCommands != null){
            System.out.println("WARNING: Registering the global commands multiple times should be avoided. Old listeners are not being removed.");
        }

        List<CommandData> commandDatas = registerListenersAndReturnCommandData(commands, null);
        jdaInstance.updateCommands().addCommands(commandDatas).queue();
        globalCommands = commands;
    }

    public static void registerGuildCommands(Guild guild, SlashCommandCollection commands){
        if (guildCommands.containsKey(guild.getId())){
            System.out.println("WARNING: Registering the commands multiple times for one guild should be avoided. Old listeners are not being removed.");
        }

        //TODO This has to be tested if it runs.
        //noinspection ConstantConditions
        List<CommandData> commandDatas = registerListenersAndReturnCommandData(commands, event -> event.isFromGuild() && event.getGuild().getId().equals(guild.getId()));
        guild.updateCommands().addCommands(commandDatas).queue();
        guildCommands.put(guild.getId(), commands);
    }

    private static List<CommandData> registerListenersAndReturnCommandData(SlashCommandCollection commandsCollection, Predicate<SlashCommandEvent> condition){
        List<CommandData> commandDatas = new ArrayList<>();
        commandsCollection.getCommands().forEach((key, value) -> {
            value.addCondition(condition);
            commandDatas.add(value.getCommandData());
            GainerBot.jdaInstance.addEventListener(value);
        });
        return commandDatas;
    }

    public static SlashCommandCollection getGlobalCommands() {
        return globalCommands;
    }

    public static SlashCommandCollection getGuildCommands(String guildId){
        return guildCommands.get(guildId);
    }
}
