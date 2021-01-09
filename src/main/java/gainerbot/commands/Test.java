package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Test extends BaseCommand {
    public Test() {
        super(new String[]{"test"}, "Sends a simple message to see if the bot works.");
        listeningChannels = IChannelPermission.Presets.getElevatedPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        event.getChannel().sendMessage("Leave me alone, you KEK!").queue();
    }
}
