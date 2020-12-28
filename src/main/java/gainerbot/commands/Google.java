package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Google extends BaseCommand {
    public Google() {
        super(new String[]{"google"}, "Creates a lmgtfy-Link with the specified input.");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        String url;
        if(options.length >= 1){
            url = "https://lmgtfy.app/?q=" + options[0].strip().replace("+", "%2B").replace(" ", "+");
        }else{
            url = "https://lmgtfy.app";
        }

        event.getChannel().sendMessage(url).queue();
    }
}
