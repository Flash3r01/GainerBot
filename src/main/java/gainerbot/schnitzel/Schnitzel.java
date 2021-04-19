package gainerbot.schnitzel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.codec.Charsets;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class Schnitzel {
    private static final String discoveredSchnitzelsFileName = "discoveredSchnitzels.txt";

    /**
     * The string that is searched for by this Schnitzel. Must contain at least 1 character.
     */
    private final String target;
    private final String response;
    private final boolean containsFiles;
    private final SchnitzelHint schnitzelHint;

    private final File schnitzelHuntUserBasePath;

    private boolean isDiscovered;

    public Schnitzel(String target, String response, boolean containsFiles, SchnitzelHint schnitzelHint, File schnitzelHuntUserBasePath) {
        this.target = target;
        this.response = response;
        this.containsFiles = containsFiles;
        this.schnitzelHint = schnitzelHint;
        this.schnitzelHuntUserBasePath = schnitzelHuntUserBasePath;

        this.isDiscovered = checkIfDiscovered();
    }

    public void sendResponse(Message message){
        StringBuilder builder = new StringBuilder();
        if(!isDiscovered){
            setDiscovered();
            builder.append("Niiice. Du hast einen Begriff gefunden!\n\n");
            message.addReaction("U+1F44D").queue();
        }
        if(containsFiles){
            // Build fileMessage
            ArrayList<File> filesToSend = new ArrayList<>();
            String[] responseLines = response.split("\n");
            for(String responseLine : responseLines){
                if(responseLine.startsWith("\\file:")){
                    String fileName = responseLine.substring(responseLine.indexOf(':')+1);
                    filesToSend.add(schnitzelHuntUserBasePath.toPath().resolve(fileName).toFile());
                }else{
                    if(builder.length() != 0) builder.append('\n');
                    builder.append(responseLine);
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
            builder.append(response);
            message.reply(builder.toString()).queue();
        }
    }

    public void sendHint(Message message){
        schnitzelHint.sendHint(message, this.getTargetCryptic());
    }

    public int distance(String input){
        input = input.toLowerCase();
        int distance = 0;
        int index = 0;
        do{
            if(index >= target.length() || index >= input.length()){
                distance += Math.abs(target.length() - input.length());
                return distance;
            }
            if(target.charAt(index) != input.charAt(index)){
                distance++;
            }
            index++;
        }while(true);
    }

    public static Schnitzel fromJSONObject(JSONObject jsonObject, File schnitzelHuntUserBasePath){
        final String target = fromPersistentRepresentation(jsonObject.getString("target")).toLowerCase();
        final String response = fromPersistentRepresentation(jsonObject.getString("response"));
        final boolean containsFiles = jsonObject.getBoolean("containsFiles");
        final SchnitzelHint schnitzelHint = SchnitzelHint.fromJSONObject(jsonObject.getJSONObject("schnitzelHint"), schnitzelHuntUserBasePath);

        return new Schnitzel(target, response, containsFiles, schnitzelHint, schnitzelHuntUserBasePath);
    }

    private boolean checkIfDiscovered() {
        File discoveredSchnitzelsFile = schnitzelHuntUserBasePath.toPath().resolve(discoveredSchnitzelsFileName).toFile();
        if(!discoveredSchnitzelsFile.exists()) return false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(discoveredSchnitzelsFile, Charsets.UTF_8));
            String line;
            while((line = reader.readLine()) != null){
                if(line.equals(target)) return true;
            }
            reader.close();
        } catch (FileNotFoundException ignored){
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException while reading file: " + discoveredSchnitzelsFile.toString());
        }

        return false;
    }

    private void setDiscovered(){
        isDiscovered = true;
        File discoveredSchnitzelsFile = schnitzelHuntUserBasePath.toPath().resolve(discoveredSchnitzelsFileName).toFile();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(discoveredSchnitzelsFile, Charsets.UTF_8, true));
            writer.write(toPersistentRepresentation(target) + '\n');
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while writing the file: " + discoveredSchnitzelsFile.toString());
        }
    }

    private static String toPersistentRepresentation(String input){
        return input.replaceAll("\n", "\\n");
    }

    private static String fromPersistentRepresentation(String input){
        return input.replaceAll("\\n", "\n");
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

    public SchnitzelHint getSchnitzelHint() {
        return schnitzelHint;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetCryptic(){
        return target.replaceAll("\\S", "\\\\*");
    }
}
