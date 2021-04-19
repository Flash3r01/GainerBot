package gainerbot.schnitzel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SchnitzelHint {

    private final String hint;
    private final boolean containsFiles;

    private final File schnitzelHuntUserBasePath;

    private SchnitzelHint(String hint, boolean containsFiles, File schnitzelHuntUserBasePath) {
        this.hint = hint;
        this.containsFiles = containsFiles;
        this.schnitzelHuntUserBasePath = schnitzelHuntUserBasePath;
    }

    public void sendHint(Message message, String crypticTarget){
        StringBuilder builder = new StringBuilder();
        builder.append("Begriff: ").append(crypticTarget).append('\n');
        if(containsFiles){
            // Build fileMessage
            ArrayList<File> filesToSend = new ArrayList<>();
            String[] hintLines = hint.split("\n");
            for(String hintLine : hintLines){
                if(hintLine.startsWith("\\file:")){
                    String fileName = hintLine.substring(hintLine.indexOf(':')+1);
                    filesToSend.add(schnitzelHuntUserBasePath.toPath().resolve(fileName).toFile());
                }else{
                    if(builder.length() != 0) builder.append('\n');
                    builder.append(hintLine);
                }
            }

            // Send fileMessage
            if(builder.length() == 0) builder.append(' ');
            MessageAction response = message.reply(builder.toString());
            for(File fileToSend : filesToSend){
                response = response.addFile(fileToSend);
            }
            response.queue();

        }else{
            builder.append('\n').append(hint);
            message.reply(builder.toString()).queue();
        }
    }

    public static SchnitzelHint fromJSONObject(JSONObject jsonObject, File schnitzelHuntUserBasePath){
        final String hint = fromPersistentRepresentation(jsonObject.getString("hint"));
        final boolean containsFiles = jsonObject.getBoolean("containsFiles");

        return new SchnitzelHint(hint, containsFiles, schnitzelHuntUserBasePath);
    }

    private static String fromPersistentRepresentation(String input){
        return input.replaceAll("\\n", "\n");
    }
}
