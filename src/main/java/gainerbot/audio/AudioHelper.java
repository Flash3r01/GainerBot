package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.file.Paths;

public class AudioHelper {

    public static boolean checkAudioRequirementsAndNotify(Member member, MessageChannel channel){
        if(member != null){
            GuildVoiceState voiceState = member.getVoiceState();

            if(voiceState != null && voiceState.inAudioChannel()){
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

    public static void connectToChannel(AudioChannel channel, AudioPlayer audioPlayer){
        if (channel == null) return;

        AudioManager audioManager = channel.getGuild().getAudioManager();
        AudioPlayerSendHandler sendHandler = new AudioPlayerSendHandler(audioPlayer);

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
            playingMessage += " (Stream)";
        }
        else{
            playingMessage += " (Length: " + Math.floorDiv(track.getDuration(), 1000) + "s.)";
        }
        playingMessage += " :musical_note:";

        return playingMessage;
    }

    public static String toTrackInfoString(AudioTrack track){
        AudioTrackInfo info = track.getInfo();
        StringBuilder builder = new StringBuilder();
        builder.append('`');

        if (info.title.equals("Unknown title") && track.getSourceManager() instanceof LocalAudioSourceManager){
            builder.append(Paths.get(track.getIdentifier()).getFileName());
        }else{
            builder.append(info.title);
        }
        builder.append("` - `");

        builder.append(info.author)
                .append("`  (");

        if (info.isStream){
            builder.append("Stream");
        }else{
            builder.append(Math.floorDiv(track.getDuration(), 1000))
                    .append('s');
        }
        builder.append(')');

        return builder.toString();
    }
}
