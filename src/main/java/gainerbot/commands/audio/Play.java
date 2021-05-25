package gainerbot.commands.audio;

import gainerbot.audio.AudioHelper;
import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;

public class Play extends BaseCommand {
    public Play() {
        super(new String[]{"play"}, "Request the GainerBot to play a song.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        if (AudioHelper.checkAudioRequirementsAndNotify(event.getMember(), event.getChannel())){
            if (arguments.length < 1){
                event.getChannel().sendMessage("Command description for play:\n" + this.description).queue();
                return;
            }else if(arguments.length == 1){
                AudioManager.getAudioManager().loadAudio(arguments[0], event.getChannel(), false);
            }else{
                String searchString = "ytsearch:" + Arrays.stream(arguments).reduce((next, acc) -> next + " " + acc).get();
                AudioManager.getAudioManager().loadAudio(searchString, event.getChannel(), false);
            }

            //noinspection ConstantConditions
            AudioHelper.connectToChannel(event.getMember().getVoiceState().getChannel());
        }
    }
}
