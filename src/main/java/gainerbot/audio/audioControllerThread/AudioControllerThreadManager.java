package gainerbot.audio.audioControllerThread;

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

public class AudioControllerThreadManager {
    private static final Map<String, AudioControllerThread> threadTracker = new HashMap<>();

    private AudioControllerThreadManager() {
    }


    public static void play(String identifier, AudioChannel audioChannel, TextChannel textChannel, Consumer<String> messageConsumer){
        getAudioControllerThread(audioChannel.getGuild().getId(), textChannel.getId()).play(identifier, audioChannel, messageConsumer);
    }

    public static void pause(SlashCommandEvent event){
        getAudioControllerThread(event).pause();
    }

    public static void resume(SlashCommandEvent event){
        getAudioControllerThread(event).resume();
    }

    public static void skip(SlashCommandEvent event){
        getAudioControllerThread(event).skip();
    }

    public static void stop(SlashCommandEvent event){
        getAudioControllerThread(event).stop();
    }

    public static void handleButtonClicked(ButtonClickEvent event){
        getAudioControllerThread(event).handleButtonClicked(event);
    }


    private static AudioControllerThread getAudioControllerThread(@NotNull SlashCommandEvent event){
        //noinspection ConstantConditions
        return getAudioControllerThread(event.getGuild().getId(), event.getGuildChannel().getId());
    }

    private static AudioControllerThread getAudioControllerThread(@NotNull ButtonClickEvent event){
        //noinspection ConstantConditions
        return getAudioControllerThread(event.getGuild().getId(), event.getGuildChannel().getId());
    }

    private static AudioControllerThread getAudioControllerThread(String guildId, String channelId){
        if (threadTracker.containsKey(guildId)) return threadTracker.get(guildId);

        Guild guild = GainerBot.jdaInstance.getGuildById(guildId);
        if (guild == null){
            throw new IllegalArgumentException("Can not get a AudioControllerMessage for an invalid guildId: "+guildId);
        }

        TextChannel outChannel = getBotChannel(guild);
        channelId = outChannel != null ? outChannel.getId() : channelId;
        AudioControllerThread newThread = new AudioControllerThread(guildId, channelId);
        threadTracker.put(guildId, newThread);
        return newThread;
    }

    private static TextChannel getBotChannel(Guild guild){
        List<TextChannel> botChannels = guild.getTextChannelsByName("gainerbot", true);
        if (botChannels.size() <= 0) return null;
        return botChannels.get(0);
    }

    public static boolean removeAudioControllerThread(String guildId){
        if (!threadTracker.containsKey(guildId)) return false;
        threadTracker.remove(guildId);
        return true;
    }

    public static AudioControllerThread getExistingAudioControllerThread(String guildId){
        return threadTracker.get(guildId);
    }
}
