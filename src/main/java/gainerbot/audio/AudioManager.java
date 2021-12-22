package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

public class AudioManager {
    private static AudioManager instance = null;

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final TrackScheduler trackScheduler;


    private AudioManager(){
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

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

    public void loadAudio(String identifier, MessageChannel logChannel, boolean priority){
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Assemble response to the user.
                String playingMessage = AudioHelper.assembleQueuedResponse(track);

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
                    List<AudioTrack> tracks = playlist.getTracks();
                    if(tracks.size() > 0){
                        AudioTrack searchResult = tracks.get(0);
                        replyStringBuilder.append("I found something!\n").append(AudioHelper.assembleQueuedResponse(searchResult));
                        trackScheduler.queue(searchResult);
                    }else{
                        logChannel.sendMessage("I searched through the entire YouTubes! But was not able to find anything :(").queue();
                        return;
                    }
                }
                else{
                    replyStringBuilder.append("Starting to play playlist: ").append(playlist.getName()).append("\n");
                    int i = 1;
                    for(AudioTrack track : playlist.getTracks()){
                        replyStringBuilder.append("Track ").append(i).append(": `").append(track.getInfo().title).append("`\n");
                        trackScheduler.queue(track);
                        i++;
                    }
                }

                logChannel.sendMessage(replyStringBuilder.toString()).queue();
            }

            @Override
            public void noMatches() {
                if(!identifier.startsWith("ytsearch:")){
                    logChannel.sendMessage("Searching YouTube for: " + identifier).queue();
                    loadAudio("ytsearch:" + identifier, logChannel, priority);
                }else{
                    logChannel.sendMessage("Audio \"" + identifier + "\" has not been found.").queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logChannel.sendMessage("Encountered an error while loading your song: " + exception.getMessage() + "\n" + exception.toString() + "\nIdentifier: " + identifier).queue();
            }
        });
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
