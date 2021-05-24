package gainerbot.commands.audio;

import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Stop extends BaseCommand {
    public Stop() {
        super(new String[]{"stop"}, "Stops the currently playing song and clears the queue.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        AudioManager.getAudioManager().getTrackScheduler().stop();
    }
}
