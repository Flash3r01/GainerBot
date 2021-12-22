package gainerbot.slashCommand.commands;

import gainerbot.GainerBot;
import gainerbot.GainerBotConfiguration;
import gainerbot.services.HttpService;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Fuck extends BaseSlashCommand {
    private static final String operationsURL = "http://foaas.com/operations";
    private static final String baseUrl = "https://foaas.com";

    public Fuck() {
        super(new CommandData("fuck", "Sends a random fuck you message")
                .addOption(OptionType.STRING, "name", "Name of recipient", false));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply().queue();

        OptionMapping nameOption = event.getOption("name");
        String requestedName = nameOption == null ? null : nameOption.getAsString();

        Member possibleMember = event.getMember();
        String requesterName = possibleMember == null ? event.getUser().getName() : possibleMember.getEffectiveName();

        String message = null;
        try {
            HttpService service = GainerBot.httpService;
            JSONArray root = new JSONArray(service.requestJsonString(new URI(operationsURL)));

            // Fill a list with the fitting urls.
            ArrayList<String> urls = new ArrayList<>();
            if(requestedName == null) {
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

            // Prepare random URL with values.
            int randomIndex = GainerBotConfiguration.random.nextInt(urls.size());
            String url = urls.get(randomIndex);
            url = url.replace(":from", "xyz123");
            if(requestedName != null){
                url = url.replace(":name", "123xyz");
            }

            JSONObject jsonResponse = new JSONObject(service.requestJsonString(new URI(baseUrl + url)));
            message = jsonResponse.getString("message") + " " + jsonResponse.getString("subtitle")
                    .replace("xyz123", requesterName);
            if (requestedName != null){
                message = message.replace("123xyz", requestedName);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send the message.
        if(message != null){
            event.getHook().sendMessage(message).queue();
        }
    }
}
