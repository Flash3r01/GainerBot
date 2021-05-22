package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.nio.file.Paths;

public class AudioManager {
    private static AudioManager instance = null;

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final TrackScheduler trackScheduler;


    private AudioManager(){
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
    }

    public static AudioManager getAudioManager(){
        if(instance == null){
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadAudio(String path, MessageChannel logChannel, boolean priority){
        playerManager.loadItem(path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Assemble response to the user.
                String playingMessage = ":musical_note: Queued `";
                if(!track.getInfo().title.equals("Unknown title")){
                    playingMessage += track.getInfo().title + "`";
                }else{
                    playingMessage += Paths.get(track.getIdentifier()).getFileName() + "`";
                }
                if(!track.getInfo().author.equals("Unknown artist")){
                    playingMessage += " by `" + track.getInfo().author + "`";
                }
                playingMessage += " | Length: " + Math.floorDiv(track.getDuration(), 1000) + "s. :musical_note:";


                if(priority){
                    trackScheduler.play(track);
                    logChannel.sendMessage(playingMessage).queue();
                }else{
                    if(trackScheduler.queue(track)){
                        logChannel.sendMessage(playingMessage).queue();
                    }else{
                        logChannel.sendMessage("Was not able to enqueue the requested track :(").queue();
                    }
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                StringBuilder replyStringBuilder = new StringBuilder();
                if (playlist.isSearchResult()){
                    replyStringBuilder.append("Playlist created by search results.\n");
                }

                replyStringBuilder.append("Starting to play playlist: ").append(playlist.getName()).append("\n");
                int i = 1;
                for(AudioTrack track : playlist.getTracks()){
                    replyStringBuilder.append("Track ").append(i).append(": `").append(track.getInfo().title).append("`\n");
                    trackScheduler.queue(track);
                    i++;
                }

                logChannel.sendMessage(replyStringBuilder.toString()).queue();
            }

            @Override
            public void noMatches() {
                logChannel.sendMessage("Audio \"" + path + "\" has not been found.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logChannel.sendMessage("Encountered an error while loading your sound: " + exception.getMessage() + "\n" + exception.toString() + "\nIdentifier: " + path).queue();
            }
        });
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
