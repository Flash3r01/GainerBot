package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import gainerbot.slashCommand.SlashCommandCollection;
import gainerbot.slashCommand.SlashCommandManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class Help extends BaseSlashCommand {
    public Help() {
        super(new CommandData("help", "Lists all commands and a short description of them"));
    }


    //TODO Add functionality to accept a commandName as an argument.
    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply(true).queue();
        Guild guild = event.getGuild();
        if (guild != null){
            event.getHook().sendMessage(getHelpString(guild.getId())).queue();
        }else{
            event.getHook().sendMessage(getHelpString()).queue();
        }
    }

    public static String getHelpString(String guildId){
        SlashCommandCollection commandCollection = SlashCommandManager.getGuildCommands(guildId);
        if (commandCollection == null) return getHelpString();

        List<BaseSlashCommand> commands = commandCollection.getCommandsAsList();
        commands.sort(Comparator.comparing(e -> e.getCommandData().getName()));
        if (commands.size() <= 0) return getHelpString();

        StringBuilder builder = new StringBuilder(getHelpString()+"\n\n\n");
        builder.append("There are also some commands that are only available in this guild!\n\n");

        appendCommands(builder, commands);

        return builder.toString();
    }

    public static String getHelpString(){
        SlashCommandCollection commandCollection = SlashCommandManager.getGlobalCommands();
        if (commandCollection == null) return "This Bot has no global commands registered.";

        List<BaseSlashCommand> commands = commandCollection.getCommandsAsList();
        commands.sort(Comparator.comparing(e -> e.getCommandData().getName()));
        if (commands.size() <= 0) return "This Bot has no global commands registered.";

        StringBuilder builder = new StringBuilder();
        builder.append("The following list contains all available commands.\nIf you want to know more about a specific command, use \"/help <commandName>\"\n\n");

        appendCommands(builder, commands);

        return builder.toString();
    }

    private static void appendCommands(StringBuilder builder, List<BaseSlashCommand> commands){
        int maxCommandLength = getMaxCommandLength(commands);

        builder.append("```\n");
        for (BaseSlashCommand command : commands){
            builder.append('/')
                    .append(command.getCommandData().getName())
                    .append(" ".repeat(maxCommandLength - command.getCommandData().getName().length()))
                    .append(" - ")
                    .append(command.getCommandData().getDescription())
                    .append('\n');
        }
        builder.append("```");
    }

    private static int getMaxCommandLength(List<BaseSlashCommand> commands){
        //noinspection OptionalGetWithoutIsPresent
        return commands.stream()
                .mapToInt(command -> command.getCommandData().getName().length())
                .max()
                .getAsInt();
    }
}
