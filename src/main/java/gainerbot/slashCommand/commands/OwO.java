package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class OwO extends BaseSlashCommand {
    public OwO() {
        super(new CommandData("owo", "UwU")
                .addOption(OptionType.STRING, "message", "Message to OwO-fy", true));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        OptionMapping messageOption = event.getOption("message");
        if (messageOption == null) return;
        String message = messageOption.getAsString();

        String responseMessage = message.replace("r", "w")
                .replace("R","W")
                .replace("l","w")
                .replace("L","W");

        event.reply(responseMessage).queue();
    }
}
