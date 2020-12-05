package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Nibba extends BaseCommand{
    public Nibba() {
        super(new String[]{"nibba"}, "changes b to :b:");
        listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        if(options.length > 0){
            String s = options[0].replace("b", ":b:").replace("B",":b:");
            event.getChannel().sendMessage(s).queue();
        }else {
            event.getChannel().sendMessage("Ni:b::b:a").queue();
        }
    }
}
