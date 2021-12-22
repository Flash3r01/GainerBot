package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class B extends BaseSlashCommand {
    public B() {
        super(new CommandData("b", "Changes b to :b:")
                .addOption(OptionType.STRING, "message", "The message to b-ify", true));
    }


    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        OptionMapping messageOption = event.getOption("message");
        if (messageOption == null) return;
        String message = messageOption.getAsString();

        String responseMessage = message.replace("b", ":b:")
                .replace("B",":b:");
        event.reply(responseMessage).queue();
    }
}
