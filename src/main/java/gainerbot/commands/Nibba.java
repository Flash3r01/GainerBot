package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Nibba extends BaseCommand{
    public Nibba() {
        super(new String[]{"nibba"}, "changes b to :b:");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        event.getChannel().sendMessage("Ni:b::b:a").queue();
    }
}
