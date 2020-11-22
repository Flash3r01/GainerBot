package gainerbot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class Watch2Gether extends BaseCommand {
    private static final String w2gUrl = "https://w2g.tv/31vo17j124wmmaf9xp";
    private TextChannel w2gChannel;

    public Watch2Gether() {
        super(new String[]{"w2g", "watch2gether"}, "Sends the Link to our Watch2Gether-Room");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        getW2GChannel(event).sendMessage("Klick mich an: " + w2gUrl).queue();
    }

    public TextChannel getW2GChannel(@Nonnull MessageReceivedEvent event){
        if(w2gChannel != null) return w2gChannel;

        List<TextChannel> textChannels = event.getGuild().getTextChannels();

        for(TextChannel textChannel : textChannels){
            if(textChannel.getName().toLowerCase().contains("watch2gether")){
                w2gChannel = textChannel;
                return w2gChannel;
            }
        }

        return null;
    }
}
