package gainerbot.audio.audioControllerThread;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioControllerThreadUpdater extends AudioEventAdapter {
    private final AudioControllerThread controllerThread;

    public AudioControllerThreadUpdater(AudioControllerThread controllerThread) {
        this.controllerThread = controllerThread;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        controllerThread.redraw();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        controllerThread.redraw();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        controllerThread.redraw();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason == AudioTrackEndReason.STOPPED){
            controllerThread.deleteAudioControllerThread();
        }
    }
}
