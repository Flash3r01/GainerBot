package gainerbot.commands;

import gainerbot.GainerBot;
import gainerbot.GainerBotConfiguration;
import gainerbot.HttpService;
import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class Fuck extends BaseCommand {
    private static final String operationsURL = "http://foaas.com/operations";
    private static final String baseUrl = "https://foaas.com";

    public Fuck() {
        super(new String[]{"fuck"}, "Sends a random fuck you message.");
        listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        if(arguments.length > 1) return;

        String message = null;
        try {
            HttpService service = GainerBot.httpService;
            JSONArray root = new JSONArray(service.requestJsonString(new URI(operationsURL)));

            //Fill a list with the fitting urls.
            ArrayList<String> urls = new ArrayList<>();
            if(arguments.length == 0) {
                for (int i = 0; i < root.length(); i++) {
                    JSONObject obj = root.getJSONObject(i);
                    JSONArray fields = obj.getJSONArray("fields");
                    if(fields.length() == 1 && fields.getJSONObject(0).getString("field").equals("from")){
                        urls.add(obj.getString("url"));
                    }
                }
            }else {
                for(int i = 0; i<root.length(); i++){
                    JSONObject obj = root.getJSONObject(i);
                    JSONArray fields = obj.getJSONArray("fields");
                    if(fields.length() == 2 &&
                            (fields.getJSONObject(0).getString("field").equals("from") ||
                            fields.getJSONObject(0).getString("field").equals("name")) &&
                            (fields.getJSONObject(1).getString("field").equals("from") ||
                            fields.getJSONObject(1).getString("field").equals("name"))){
                        urls.add(obj.getString("url"));
                    }
                }
            }

            //Prepare random URL with values.
            int randomIndex = GainerBotConfiguration.random.nextInt(urls.size());
            String url = urls.get(randomIndex);
            url = url.replace(":from", "ppllaacceehhoollddeerr");
            if(arguments.length == 1){
                url = url.replace(":name", arguments[0]);
            }

            JSONObject jsonResponse = new JSONObject(service.requestJsonString(new URI(baseUrl + url)));
            message = jsonResponse.getString("message") + " " + jsonResponse.getString("subtitle").replace("ppllaacceehhoollddeerr", event.getAuthor().getName());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Send the message and delete command.
        if(message != null){
            event.getMessage().delete().queue();
            event.getChannel().sendMessage(message).queue();
        }
    }
}
