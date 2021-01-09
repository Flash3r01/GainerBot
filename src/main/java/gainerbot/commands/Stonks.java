package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Stonks extends BaseCommand {

    public Stonks() {
        super(new String[]{"stonk", "stonks"}, "Creates a fitting link to seeking alpha.");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        String url = "https://seekingalpha.com/";

        if(arguments.length >= 1){
            url += "symbol/" + arguments[0].toUpperCase();
        }

        event.getChannel().sendMessage(url).queue();
    }
}
