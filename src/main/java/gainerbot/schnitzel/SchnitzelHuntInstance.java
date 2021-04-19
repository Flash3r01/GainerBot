package gainerbot.schnitzel;

import gainerbot.GainerBot;
import gainerbot.GainerBotConfiguration;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.codec.Charsets;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the schnitzelHunt for one user.
 */
public class SchnitzelHuntInstance {
    private static final String schnitzelsFileName = "schnitzels.json";
    private static final String secretFileName = "secret.txt";
    private static final String lastHintTimeFileName = "hintTime.txt";
    private static final String logFileName = "log.txt";
    private static final int distanceToNotify = 3;

    private final File schnitzelHuntUserBasePath;
    private final String userId;
    private final ArrayList<Schnitzel> schnitzels;

    private int attemptCounter;

    private SchnitzelHuntInstance(File schnitzelHuntUserBasePath) {
        this.schnitzelHuntUserBasePath = schnitzelHuntUserBasePath;
        this.userId = schnitzelHuntUserBasePath.toPath().getFileName().toString();
        this.schnitzels = new ArrayList<>();
        this.attemptCounter = 0;

        loadSchnitzelsFromFile(schnitzelHuntUserBasePath.toPath().resolve(schnitzelsFileName).toFile());
        welcomeUser();
    }

    private void welcomeUser() {
        if(schnitzels.stream().noneMatch(Schnitzel::isDiscovered)){
            GainerBot.jdaInstance.openPrivateChannelById(userId).queue(this::sendWelcomeMessage);
        }
    }

    public static SchnitzelHuntInstance fromBaseFile(File schnitzelHuntUserBasePath){
        return new SchnitzelHuntInstance(schnitzelHuntUserBasePath);
    }

    public void handleMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();

        // Log the message
        this.log(msg);

        // Prepare the message
        msg = msg.strip().toLowerCase();
        String nonRepeatingMsg = removeRepeatingChars(msg);

        // Check for commands (Status, Hint and geheimnis)
        if(nonRepeatingMsg.contains("help") || nonRepeatingMsg.contains("hilfe") || nonRepeatingMsg.contains("hint")){
            requestHint(event.getMessage());
            return;
        }
        if(nonRepeatingMsg.contains("status") || nonRepeatingMsg.contains("stats")){
            sendStats(event.getMessage());
            return;
        }
        if(nonRepeatingMsg.contains("geheimnis")){
            if(schnitzels.stream().allMatch(Schnitzel::isDiscovered)){
                sendSecret(event.getChannel());
            }else{
                event.getChannel().sendMessage("Du... kannst nicht... vorbeiiiiii!\n" +
                        "Außer natürlich du errätst noch ein par Begriffe :sweat_smile:").queue();
            }
            return;
        }

        // Check target hits
        String finalMsg = msg;
        List<Schnitzel> matchingSchnitzels = schnitzels.stream().filter((item) -> finalMsg.equals(item.getTarget())).collect(Collectors.toList());
        if(matchingSchnitzels.size() == 1){
            boolean foundAll = schnitzels.stream().allMatch(Schnitzel::isDiscovered);
            matchingSchnitzels.get(0).sendResponse(event.getMessage());
            if(!foundAll){
                if(schnitzels.stream().allMatch(Schnitzel::isDiscovered)){
                    event.getChannel().sendMessage("Warte... Das kann nicht sein. Du sollst alle meine Begriffe erraten haben?\n" +
                            "Wenn das stimmt, dann müsste ich jetzt ja darauf reagieren, wenn du mich nach meinem Geheimnis fragst! O.O").queue();
                }
            }
            attemptCounter = (attemptCounter > 0 ? 0 : -20);
            return;
        }

        // Check close to target
        matchingSchnitzels = schnitzels.stream()
                .filter((item) -> item.distance(finalMsg) <= distanceToNotify)
                .filter((item) -> !item.isDiscovered())
                .collect(Collectors.toList());
        if(matchingSchnitzels.size() != 0){
            sendCloseSchnitzelsMessage(event.getMessage());
            return;
        }

        // Tell the sender he missed
        event.getMessage().addReaction("U+274C").queue();
        event.getMessage().reply("Nope, daran hab ich nicht gedacht :(. Aber versuchs doch gleich nochmal ;)").queue();
        attemptCounter++;

        // If to many attempts have been made, remind of hint
        if(attemptCounter > 6){
            event.getChannel().sendMessage("Vergiss nicht dass du mich um Hilfe fragen kannst!\n" +
                    "Versuchs mal mit: \"Oh neim lieber GainerBot. Ich komme einfach nicht weiter... **Hilfeeeeeee**\"\n\n" +
                    "Und wenn ich grade dabei bin: Vergiss nicht dass du dir deinen Fortschritt (**Status**) anzeigen lassen kannst ;)").queue();
            attemptCounter = -20;
        }
    }

    private void log(String msg) {
        try {
            File logFile = schnitzelHuntUserBasePath.toPath().resolve(logFileName).toFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, Charsets.UTF_8, true));
            String prefix = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] : ";
            writer.write(prefix + msg + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSecret(MessageChannel channel){
        File secretFile = schnitzelHuntUserBasePath.toPath().resolve(secretFileName).toFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(secretFile, Charsets.UTF_8));
            String secretMessage = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            channel.sendMessage(secretMessage).queue(message ->
                    message.addReaction("U+1F49D")
                            .and(message.addReaction("U+1F49E"))
                            .and(message.addReaction("U+1F496"))
                            .and(message.addReaction("U+1F495"))
                            .and(message.addReaction("U+2763")).queue()
            );
        } catch (IOException e) {
            e.printStackTrace();
            channel.sendMessage("Ähm... Es sieht so aus als ob ich vergessen habe wo ich mein Geheimnis verstecke ^^'\nWenn du dem Kundensupport Bescheid gibst erinner ich mich bestimmt wieder dran :P.").queue();
        }
    }

    private void sendStats(Message message) {
        StringBuilder builder = new StringBuilder();
        List<Schnitzel> foundSchnitzels = schnitzels.stream().filter(Schnitzel::isDiscovered).collect(Collectors.toList());
        if(foundSchnitzels.size() != 0) {
            builder.append("Hier sind die ").append(foundSchnitzels.size()).append(" Begriffe die du schon gefunden hast:\n");
            for (Schnitzel foundSchnitzel : foundSchnitzels) {
                builder.append(foundSchnitzel.getTarget());
                builder.append('\n');
            }
            builder.append('\n');
        }

        List<Schnitzel> notFoundSchnitzels = schnitzels.stream().filter((item) -> !item.isDiscovered()).collect(Collectors.toList());
        if(notFoundSchnitzels.size() != 0) {
            builder.append("Und hier die ").append(notFoundSchnitzels.size()).append(" Begriffe die dir noch fehlen... Natürlich nicht in Klartext :P:\n");
            for (Schnitzel notFoundSchnitzel : notFoundSchnitzels) {
                builder.append(notFoundSchnitzel.getTargetCryptic());
                builder.append('\n');
            }
        }

        message.reply(builder.toString()).queue();
    }

    private String removeRepeatingChars(String input) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < input.length()-1; i++){
            if(input.charAt(i) != input.charAt(i+1)){
                builder.append(input.charAt(i));
            }
        }
        builder.append(input.charAt(input.length()-1));
        return builder.toString();
    }

    private void requestHint(Message message) {
        final long secondsBetweenHints = 60;
        // Calculate time since last hint.
        long secondsSinceLastHint = Long.MAX_VALUE;
        File lastHintTimeFile = schnitzelHuntUserBasePath.toPath().resolve(lastHintTimeFileName).toFile();
        if(lastHintTimeFile.exists()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(lastHintTimeFile, Charsets.UTF_8));
                long lastHintTime = Long.parseLong(reader.readLine());
                reader.close();
                secondsSinceLastHint = (System.currentTimeMillis()-lastHintTime)/1000;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException while reading the lastHintTimeFile of user: "+userId);
            }
        }

        // Get and send a random hint, if allowed.
        if(secondsSinceLastHint > secondsBetweenHints) {
            List<Schnitzel> undiscoveredSchnitzels = schnitzels.stream().filter((item) -> !item.isDiscovered()).collect(Collectors.toList());

            if (undiscoveredSchnitzels.size() == 0) {
                message.reply("Uhm... Ich glaube dir ist nicht mehr zu helfen...\nWeil du fertig biiiiiiist! Yaaaaaay :heart:").queue();
            } else {
                int rndIndex = GainerBotConfiguration.random.nextInt(undiscoveredSchnitzels.size());
                undiscoveredSchnitzels.get(rndIndex).sendHint(message);

                // Save the current time
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(lastHintTimeFile, Charsets.UTF_8, false));
                    writer.write(Long.toString(System.currentTimeMillis()));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Was not able to write current time to file for user: "+userId);
                }
            }
        }else{
            message.reply(":open_mouth: Immer sachte mit den jungen Pferden!\n" +
                    "Du scheinst ja ganz schön stuck zu sein, wenn du so viel Hilfe brauchst. Vielleicht solltest du deinen Step-Bot um Hilfe bitten :smirk:.\n" +
                    "Ich kann dir leider erst wieder in **" + (secondsBetweenHints-secondsSinceLastHint) + "s** helfen. ¯\\_(ツ)_/¯").queue();
        }
    }

    private void sendWelcomeMessage(MessageChannel channel){
        String welcomeMsg = "Henlo! Du hast mich gefunden O.O.\n" +
                "Aber an mein Geheimnis kommst du trotzdem nicht ran :smiling_imp:\n\n" +
                "Weißt du was? Ich geb dir ne *Chance*.\n" +
                "Ich denke mir " + schnitzels.size() + " Begriffe aus. Wenn du es schaffst jeden einzelnen davon zu erraten erzähle ich dir mein Geheimnis. Fast alle Begriffe haben auch was mit uns zu tun :wink:\n\n" +
                "Wenn du mir einen Begriff vorschlagen möchtest, schreib ihn mir einfach. Ich antworte dann darauf... Wenn auch manchmal ein bisschen langsam. Internet der Hurensohn.\n" +
                "Falls du wissen möchtest wie du dich anstellst, kannst du mich nach dem Status fragen\n" +
                "Und wenn du um Hilfe rufst, werde ich dich bestimmt nicht einfach ignorieren :3\n\n" +
                "Wenn (oder eher wann) etwas schief läuft, frag einfach deinen " + (userId.endsWith("57355065114634") ? "Herr und Gebieter :P\n\n" : "Kundensupport\n\n") +
                "**__Und jetzt viel Spaß mit mir ^-^__**";
        channel.sendMessage(welcomeMsg).queue();
    }

    private void sendCloseSchnitzelsMessage(Message message) {
        message.reply("Fast!.. Aber nicht ganz. Versuchs nochmal :3\nAn mindestens einem unentdeckten Begriff bist du gaaaanz knapp dran ;).").queue();
    }

    private void loadSchnitzelsFromFile(File schnitzelsFile){
        String fileContent = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(schnitzelsFile, Charsets.UTF_8));
            fileContent = reader.lines().collect(Collectors.joining());
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("The schnitzelsFile " + schnitzelsFile.toString() + " does not exist.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Exception while trying to read the schnitzelsFile.");
        }
        if(fileContent == null) return;

        JSONArray root = new JSONArray(fileContent);
        List<Schnitzel> tmp = new ArrayList<>();
        for(Object persistentSchnitzelObj : root){
            tmp.add(Schnitzel.fromJSONObject((JSONObject) persistentSchnitzelObj, schnitzelHuntUserBasePath));
        }
        tmp = tmp.stream().sorted(Comparator.comparingInt(o -> o.getTarget().charAt(0))).collect(Collectors.toList());
        schnitzels.addAll(tmp);
    }
}
