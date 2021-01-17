package gainerbot.commands;

import gainerbot.GainerBot;
import gainerbot.permissions.IChannelPermission;
import gainerbot.services.HttpService;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Pog extends BaseCommand{
    private static final String Url = "https://pogchamp.today/";
    private MessageChannel _pogChannel;

    public Pog() {
        super(new String[]{"pog", "potd"}, "Posts the current PogChamp Emote in the PogChamp-Channel.");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        HttpService service = GainerBot.httpService;
        try {
            String response = service.requestGeneric(new URI(Url));
            String pogChampURL = extractURLFromPogchampToday(response);

            if(pogChampURL == null) return;

            getPogChannel(event).sendMessage(pogChampURL).queue();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String extractURLFromPogchampToday(String html){
        int index = html.indexOf(" alt=\"PogChamp\"");
        if(index == -1) return null;

        while(html.charAt(index) != '\"'){
            index--;
        }
        int endIndex = index;
        index--;
        while(html.charAt(index) != '\"'){
            index--;
        }
        return html.substring(index+1, endIndex);
    }

    private MessageChannel getPogChannel(@Nonnull MessageReceivedEvent event){
        if(_pogChannel == null) {
            for (TextChannel channel : event.getGuild().getTextChannels()) {
                if (channel.getName().toLowerCase().contains("pogchamp")) _pogChannel = channel;
            }
        }

        return _pogChannel;
    }
}
