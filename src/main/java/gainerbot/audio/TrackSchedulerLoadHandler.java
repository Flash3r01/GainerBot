package gainerbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.function.BiConsumer;

public class TrackSchedulerLoadHandler implements AudioLoadResultHandler {
    private final TrackScheduler scheduler;
    private final String identifier;
    private final BiConsumer<String, Boolean> messageConsumer;

    //TODO It is not that pretty to pass the identifier in the constructor :/
    public TrackSchedulerLoadHandler(TrackScheduler scheduler, String identifier, BiConsumer<String, Boolean> messageConsumer) {
        this.scheduler = scheduler;
        this.identifier = identifier;
        this.messageConsumer = messageConsumer;
    }


    @Override
    public void trackLoaded(AudioTrack track) {
        if (scheduler.queue(track)){
            messageConsumer.accept(AudioHelper.assembleQueuedResponse(track), true);
        }else {
            messageConsumer.accept("Was not able to enqueue the requested track :(.", false);
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()){
            List<AudioTrack> tracks = playlist.getTracks();
            if(tracks.size() > 0){
                trackLoaded(tracks.get(0));
            }else{
                messageConsumer.accept("Youtube search for \"" + identifier.replaceFirst("ytsearch:", "") + "\" gives no results.", false);
            }
            return;
        }

        StringBuilder replyStringBuilder = new StringBuilder();
        replyStringBuilder.append("Queuing playlist: ").append(playlist.getName()).append("\n");
        int i = 1;
        for(AudioTrack track : playlist.getTracks()){
            replyStringBuilder.append("Track ").append(i).append(": `").append(track.getInfo().title).append("`\n");
            scheduler.queue(track);
            i++;
        }

        messageConsumer.accept(replyStringBuilder.toString(), true);
    }

    @Override
    public void noMatches() {
        if(!identifier.startsWith("ytsearch:")){
            //messageConsumer.accept("\"" + identifier + "\" is not an audio link. Searching Youtube.");
            loadAudio("ytsearch:" + identifier, scheduler, messageConsumer);
        }else{
            messageConsumer.accept("Failed to play \"" + identifier.replaceFirst("ytsearch:", "") + "\". Did not find anything to play.", false);
        }
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        messageConsumer.accept("Encountered an error while loading audio: " + exception.getMessage() + "\n" + exception.toString() + "\nIdentifier: " + identifier, false);
    }

    public static void loadAudio(String identifier, TrackScheduler scheduler, BiConsumer<String, Boolean> messageConsumer){
        AudioManager.getAudioManager().getPlayerManager().loadItem(identifier, new TrackSchedulerLoadHandler(scheduler, identifier, messageConsumer));
    }
}
