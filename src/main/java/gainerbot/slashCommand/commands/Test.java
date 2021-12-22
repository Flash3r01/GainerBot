package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Test extends BaseSlashCommand {
    public Test() {
        super(new CommandData("test", "Test the Bot. He replies with a message."));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.reply("Beep, boop. I am alive!").queue();
    }
}
