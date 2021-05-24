package gainerbot.commands.audio;

import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Pause extends BaseCommand {
    public Pause() {
        super(new String[]{"pause"}, "Pauses the playback.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        AudioManager.getAudioManager().getTrackScheduler().pause(true);
    }
}
