package gainerbot.slashCommand.commands.audio.audioControllerMessage;

import gainerbot.GainerBot;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AudioControllerMessageManager {
    private static final Map<String, AudioControllerMessage> messageTracker = new HashMap<>();

    private AudioControllerMessageManager() {
    }


    public static void play(String identifier, AudioChannel audioChannel, TextChannel textChannel, Consumer<String> messageConsumer){
        getAudioControllerMessage(audioChannel.getGuild().getId(), textChannel.getId()).play(identifier, audioChannel, messageConsumer);
    }

    public static void pause(SlashCommandEvent event){
        getAudioControllerMessage(event).pause();
    }

    public static void resume(SlashCommandEvent event){
        getAudioControllerMessage(event).resume();
    }

    public static void skip(SlashCommandEvent event){
        getAudioControllerMessage(event).skip();
    }

    public static void stop(SlashCommandEvent event){
        getAudioControllerMessage(event).stop();
    }

    public static void handleButtonClicked(ButtonClickEvent event){
        getAudioControllerMessage(event).handleButtonClicked(event);
    }


    private static AudioControllerMessage getAudioControllerMessage(@NotNull SlashCommandEvent event){
        //noinspection ConstantConditions
        return getAudioControllerMessage(event.getGuild().getId(), event.getGuildChannel().getId());
    }

    private static AudioControllerMessage getAudioControllerMessage(@NotNull ButtonClickEvent event){
        //noinspection ConstantConditions
        return getAudioControllerMessage(event.getGuild().getId(), event.getGuildChannel().getId());
    }

    private static AudioControllerMessage getAudioControllerMessage(String guildId, String channelId){
        if (messageTracker.containsKey(guildId)) return messageTracker.get(guildId);

        Guild guild = GainerBot.jdaInstance.getGuildById(guildId);
        if (guild == null){
            throw new IllegalArgumentException("Can not get a AudioControllerMessage for an invalid guildId: "+guildId);
        }

        TextChannel outChannel = getBotChannel(guild);
        channelId = outChannel != null ? outChannel.getId() : channelId;
        AudioControllerMessage newMessage = new AudioControllerMessage(guildId, channelId);
        messageTracker.put(guildId, newMessage);
        return newMessage;
    }

    private static TextChannel getBotChannel(Guild guild){
        List<TextChannel> botChannels = guild.getTextChannelsByName("gainerbot", true);
        if (botChannels.size() <= 0) return null;
        return botChannels.get(0);
    }

    public static boolean removeAudioControllerMessage(String guildId){
        if (!messageTracker.containsKey(guildId)) return false;
        messageTracker.remove(guildId);
        return true;
    }

    public static AudioControllerMessage getExistingAudioControllerMessage(String guildId){
        return messageTracker.get(guildId);
    }
}
