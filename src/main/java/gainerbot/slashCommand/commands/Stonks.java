package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Stonks extends BaseSlashCommand {
    private final String baseURL = "https://seekingalpha.com/symbol/";

    public Stonks() {
        super(new CommandData("stonks", "Generates a link to Seeking Alpha for symbols")
                .addOption(OptionType.STRING, "symbol", "The symbol to generate the link for", true));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        OptionMapping symbolOption = event.getOption("symbol");
        if (symbolOption == null) return;
        String symbol = symbolOption.getAsString();

        String reply = "Here is your Link:\n" + baseURL + symbol.toUpperCase();

        event.reply(reply).setEphemeral(true).queue();
    }
}
