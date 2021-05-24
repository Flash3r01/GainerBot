package gainerbot.commands.audio;

import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Resume extends BaseCommand {
    public Resume() {
        super(new String[]{"resume", "unpause"}, "Resumes the playback.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        AudioManager.getAudioManager().getTrackScheduler().pause(false);
    }
}
