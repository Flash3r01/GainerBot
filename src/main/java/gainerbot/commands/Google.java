package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Google extends BaseCommand {
    public Google() {
        super(new String[]{"google"}, "Creates a lmgtfy-Link with the specified input.");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        String url;
        if(arguments.length >= 1){
            url = "https://lmgtfy.app/?q=" + arguments[0].strip().replace("+", "%2B").replace(" ", "+");
        }else{
            url = "https://lmgtfy.app";
        }

        event.getChannel().sendMessage(url).queue();
    }
}
