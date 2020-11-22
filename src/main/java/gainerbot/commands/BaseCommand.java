package gainerbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public abstract class BaseCommand {
    public final String[] names;
    public final String description;

    protected BaseCommand(String[] names, String description) {
        this.names = names;
        this.description = description;
    }

    public abstract void execute(@Nonnull MessageReceivedEvent event, String[] options);
}
