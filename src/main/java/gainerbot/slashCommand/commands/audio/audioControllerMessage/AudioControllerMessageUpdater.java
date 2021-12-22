package gainerbot.slashCommand.commands.audio.audioControllerMessage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioControllerMessageUpdater extends AudioEventAdapter {
    private final AudioControllerMessage controllerMessage;

    public AudioControllerMessageUpdater(AudioControllerMessage controllerMessage) {
        this.controllerMessage = controllerMessage;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        controllerMessage.redraw();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        controllerMessage.redraw();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        controllerMessage.redraw();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason == AudioTrackEndReason.STOPPED){
            controllerMessage.deleteAudioControllerMessage();
        }
    }
}
