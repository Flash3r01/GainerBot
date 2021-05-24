package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import jdk.jshell.spi.ExecutionControl;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    private AudioTrack replacedTrack = null;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>(50);
    }

    /**
     * Adds a track to the end of the queue.
     * @param track The track to add to the queue.
     * @return False, if the track was not added to the queue. True otherwise.
     */
    public boolean queue(AudioTrack track) {
        if(!player.startTrack(track, true)){
            return queue.offer(track);
        }
        return true;
    }

    /**
     * Immediately starts playing a track and resumes the replaced track after its finished.
     * @param track The track to start playing.
     */
    public void play(AudioTrack track) {
        replacedTrack = player.getPlayingTrack();
        player.startTrack(track, false);
    }

    /**
     * Skips the currently playing track and starts the next one in queue.
     */
    public void skip(){
        player.startTrack(queue.poll(), false);
    }

    /**
     * Stops the currently playing track. Also clears the queue.
     */
    public void stop(){
        player.setPaused(false);
        player.stopTrack();
    }

    /**
     * Pauses or resumes the currently playing track.
     * @param value If the player should be paused
     */
    public void pause(boolean value){
        player.setPaused(value);
    }

    //TODO Implement the Track Scheduler
    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            loadNextTrack();
        }
        else if (endReason == AudioTrackEndReason.STOPPED){
            queue.clear();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
        //TODO Send a message to inform someone.
        System.out.println("EXCEPTION: Track stopped: " + track.getInfo().title + "\n Exception message: " + exception.getMessage() + "\n Stacktrace: " + Arrays.toString(exception.getStackTrace()).replaceAll(", ", "\n  ") + "\n Cause: " + exception.getCause());
        loadNextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        //TODO Send a message to inform someone.
        System.out.println("Track stuck! Title: " + track.getInfo().title + "\nURI: " + track.getInfo().uri);
        loadNextTrack();
    }

    private void loadNextTrack(){
        // Resume the replaced track
        if (replacedTrack != null) {
            if (player.startTrack(replacedTrack, true)){
                replacedTrack = null;
            }
        }else{
            // Start next track from queue
            player.startTrack(queue.poll(), false);
        }
    }
}