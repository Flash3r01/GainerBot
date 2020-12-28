package gainerbot.commands;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public abstract class BaseCommand {
    public final String[] names;
    public final String description;

    protected IChannelPermission listeningChannels = IChannelPermission.Presets.getBotPermissions();

    protected BaseCommand(String[] names, String description) {
        this.names = names;
        this.description = description;
    }

    public boolean listensOnChannel(String channelName){
        return listeningChannels.isAllowed(channelName);
    }

    public abstract void execute(@Nonnull MessageReceivedEvent event, String[] options);
}
