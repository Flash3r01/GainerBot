package gainerbot.commands.audio;

import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Skip extends BaseCommand {
    public Skip() {
        super(new String[]{"skip"}, "Skips the currently playing song.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        AudioManager.getAudioManager().getTrackScheduler().skip();
    }
}
