package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioManager {
    private static AudioManager instance = null;

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;

    private AudioTrack currentLoadedTrack = null;

    private AudioManager(){
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        //playerManager.setFrameBufferDuration();
        //TODO Set the player to opus encoding?

        player = playerManager.createPlayer();
        player.addListener(new TrackScheduler());
    }

    public static AudioManager getAudioManager(){
        if(instance == null){
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadAudio(String path){
        //TODO fill this with info.
        playerManager.loadItem(path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
