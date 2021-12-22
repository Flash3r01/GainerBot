package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Help extends BaseSlashCommand {
    public Help() {
        super(new CommandData("help", "Lists all commands and a short description of them"));
    }


    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        // TODO Implement the help command in the new command system.
        event.reply("The /help command has not yet been implemented with the new command system. Sorry!").setEphemeral(true).queue();
    }
}
