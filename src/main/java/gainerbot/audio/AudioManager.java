package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageChannel;

public class AudioManager {
    private static AudioManager instance = null;

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;


    private AudioManager(){
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        player = playerManager.createPlayer();
        player.addListener(new TrackScheduler());
    }

    public static AudioManager getAudioManager(){
        if(instance == null){
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadAudio(String path, MessageChannel logChannel){
        playerManager.loadItem(path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logChannel.sendMessage("Playing: " + track.getInfo().title).queue();
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logChannel.sendMessage("Playing Playlist: " + playlist.getName()).queue();
                //TODO Play the playlist.
            }

            @Override
            public void noMatches() {
                logChannel.sendMessage("Audio with identifier \"" + path + "\" has not been found.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logChannel.sendMessage("Encountered an error while loading your sound: " + exception.toString() + "\nIdentifier: " + path).queue();
            }
        });
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
