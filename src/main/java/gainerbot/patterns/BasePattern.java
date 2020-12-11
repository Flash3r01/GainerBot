package gainerbot.patterns;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public abstract class BasePattern {
    protected final String name;
    protected final String description;

    protected boolean isActive = true;
    protected IChannelPermission listeningChannels = IChannelPermission.Presets.getStandardPermissions();

    protected BasePattern(String name, String description){
        this.name = name;
        this.description = description;
    }

    public boolean listensOnChannel(MessageChannel channel){
        return listeningChannels.isAllowed(channel.getName());
    }

    public abstract void run(@Nonnull MessageReceivedEvent event);

    //region Getter & Setter
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    //endregion
}
