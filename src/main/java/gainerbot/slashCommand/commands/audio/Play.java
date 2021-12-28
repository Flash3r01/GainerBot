package gainerbot.slashCommand.commands.audio;

import gainerbot.audio.audioControllerThread.AudioControllerThreadManager;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Play extends BaseSlashCommand {
    public Play() {
        super(new CommandData("play", "Plays audio")
                .addOption(OptionType.STRING, "identifier", "Link to audio or youtube search query", true));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply(true).queue();

        OptionMapping identifierOption = event.getOption("identifier");
        if (identifierOption == null) return;
        String identifier = identifierOption.getAsString();

        Member member = event.getMember();
        if (member == null){
            event.getHook().sendMessage("This command has to be used in a guild!").setEphemeral(true).queue();
            return;
        }
        //noinspection ConstantConditions
        AudioChannel audioChannel = member.getVoiceState().getChannel();
        if (audioChannel == null){
            event.getHook().sendMessage("You have to be in a voice channel to use this command!").setEphemeral(true).queue();
            return;
        }

        AudioControllerThreadManager.play(identifier, audioChannel, event.getTextChannel(), (msg) -> event.getHook().sendMessage(msg).setEphemeral(true).queue());
    }

    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        AudioControllerThreadManager.handleButtonClicked(event);
    }
}
