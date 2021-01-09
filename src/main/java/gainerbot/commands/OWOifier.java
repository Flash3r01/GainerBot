package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class OWOifier extends BaseCommand{
    public OWOifier() {
        super(new String[]{"owo", "OwO", "OWO"}, "UwU");
        listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        if(arguments.length > 0){
            String s = arguments[0].replace("r", "w").replace("R","W").replace("l","w").replace("L","W");
            event.getChannel().sendMessage(s).queue();
        }else {
            event.getChannel().sendMessage("UwU gimme text").queue();
        }
    }
}
