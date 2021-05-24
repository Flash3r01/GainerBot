package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.file.Paths;

public class AudioHelper {

    public static boolean checkAudioRequirementsAndNotify(Member member, MessageChannel channel){
        if(member != null){
            GuildVoiceState voiceState = member.getVoiceState();

            if(voiceState != null && voiceState.inVoiceChannel()){
                return true;
            }else{
                channel.sendMessage("You have to be in a Voice Channel to use this command.").queue();
                return false;
            }

        }else{
            channel.sendMessage("You have to use this command in a Server.").queue();
            return false;
        }
    }

    public static void connectToChannel(VoiceChannel channel){
        if(channel == null) return;

        AudioManager audioManager = channel.getGuild().getAudioManager();
        AudioPlayerSendHandler sendHandler = new AudioPlayerSendHandler(gainerbot.audio.AudioManager.getAudioManager().getPlayer());

        audioManager.setSendingHandler(sendHandler);
        audioManager.openAudioConnection(channel);
    }

    public static String assembleQueuedResponse(AudioTrack track){
        String playingMessage = ":musical_note: Queued `";
        if(!track.getInfo().title.equals("Unknown title")){
            playingMessage += track.getInfo().title + "`";
        }else if (track.getSourceManager() instanceof LocalAudioSourceManager){
            playingMessage += Paths.get(track.getIdentifier()).getFileName() + "`";
        }else{
            playingMessage += "Unknown title`";
        }
        if(!track.getInfo().author.equals("Unknown artist")){
            playingMessage += " by `" + track.getInfo().author + "`";
        }
        if(track.getInfo().isStream) {
            playingMessage += " | Stream";
        }
        else{
            playingMessage += " | Length: " + Math.floorDiv(track.getDuration(), 1000) + "s.";
        }
        playingMessage += " :musical_note:";

        return playingMessage;
    }
}
