package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Google extends BaseSlashCommand {
    public Google() {
        super(new CommandData("google", "Creates a lmgtfy-Link with the specified query.")
                .addOption(OptionType.STRING, "query", "The query lmgtfy inserts", true));
    }


    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply().setEphemeral(false).queue();
        OptionMapping queryOption = event.getOption("query");
        if (queryOption == null) return;
        String query = queryOption.getAsString();

        String url = "https://lmgtfy.app/?q=" + query.strip().replace("+", "%2B").replace(" ", "+");

        event.getHook().sendMessage(url).queue();
    }
}
