package gainerbot.commands;

import gainerbot.permissions.WhiteList;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Nibba extends BaseCommand{
    public Nibba() {
        super(new String[]{"nibba"}, "changes b to :b:");
        listeningChannels = new WhiteList(new String[]{"ni\uD83C\uDD71\uD83C\uDD71a"}, new String[0]);
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
