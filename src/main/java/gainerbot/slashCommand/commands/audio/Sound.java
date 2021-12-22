package gainerbot.slashCommand.commands.audio;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Sound extends BaseSlashCommand {
    public Sound() {
        super(new CommandData("sound", "Plays a sound from a small selection.")
                .addOption(OptionType.STRING, "name", "The name of the sound", false));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        //TODO Implement the sound command in the new command system.
        event.reply("The /sound command has not yet been implemented with the new command system. Sorry!").setEphemeral(true).queue();
    }
}
