package gainerbot;

import gainerbot.audio.AudioManager;
import gainerbot.audio.audioControllerThread.AudioControllerThread;
import gainerbot.audio.audioControllerThread.AudioControllerThreadManager;
import gainerbot.patterns.Loiny;
import gainerbot.schnitzel.SchnitzelHuntManager;
import gainerbot.services.HttpService;
import gainerbot.slashCommand.SlashCommandCollection;
import gainerbot.slashCommand.SlashCommandManager;
import gainerbot.slashCommand.commands.*;
import gainerbot.slashCommand.commands.audio.Play;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GainerBot extends ListenerAdapter {
    public static JDA jdaInstance;

    public static GainerBotCommands commandManager;
    public static GainerBotPatterns patternManager;
    public static SchnitzelHuntManager schnitzelHuntManager;
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static final HttpService httpService = new HttpService();

    public static void startGainerBot(){
        if(jdaInstance != null){
            System.out.println("There is already a GainerBot running.");
            return;
        }

        //TODO read all the configuration at once.
        String token = "";
        try {
            Path path;
            if (!GainerBotConfiguration.isDebug){
                path = Paths.get(GainerBotConfiguration.basePath.toString(), "token.txt");
            }else {
                path = Paths.get(GainerBotConfiguration.basePath.toString(), "debugToken.txt");
            }
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            token = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find the Token-File.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read the Token from the File.");
        }

        MemberCachePolicy policy = MemberCachePolicy.ALL;

        //Create the Bot Instance
        JDABuilder builder = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("Schnapp - Gzuz"))
                .disableCache(CacheFlag.ACTIVITY)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(policy)
                .addEventListeners(new GainerBot());
                //.setCompression(Compression.NONE);

        //TODO add an actual logger.
        try {
            jdaInstance = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.out.println("Could not login.");
        }
        try {
            jdaInstance.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("A needed Thread was interrupted.");
        }

        //commandManager = new GainerBotCommands();
        patternManager = new GainerBotPatterns();
        schnitzelHuntManager = SchnitzelHuntManager.getSchnitzelManager();
        initSlashCommands();

        for(Guild guild : jdaInstance.getGuilds()){
            guild.loadMembers().onSuccess(list -> System.out.println("Members loaded for guild: " + guild.getName()));
        }
        System.out.println("GainerBot started.");

        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
    }

    private static void initSlashCommands() {
        SlashCommandCollection globalCommandCollection = new SlashCommandCollection();
        if (!GainerBotConfiguration.isDebug){
            globalCommandCollection.addCommand(new B());
            globalCommandCollection.addCommand(new Fuck());
            globalCommandCollection.addCommand(new Google());
            globalCommandCollection.addCommand(new Help());
            globalCommandCollection.addCommand(new OwO());
            globalCommandCollection.addCommand(new Pasta());
            globalCommandCollection.addCommand(new Pattern());
            globalCommandCollection.addCommand(new Play());
            globalCommandCollection.addCommand(new Status());
            globalCommandCollection.addCommand(new Stonks());
            globalCommandCollection.addCommand(new Surprise());
            globalCommandCollection.addCommand(new Watch2Gether());
            SlashCommandManager.registerGlobalCommands(jdaInstance, globalCommandCollection);
        }
        else{
            globalCommandCollection.addCommand(new Help());
            globalCommandCollection.addCommand(new Pattern());
            globalCommandCollection.addCommand(new Play());
            globalCommandCollection.addCommand(new Status());
            SlashCommandManager.registerGuildCommands(jdaInstance.getGuildById(GainerBotConfiguration.debugGuildId), globalCommandCollection);
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(!event.getMessage().isFromType(ChannelType.PRIVATE)) {
            if (commandManager != null) commandManager.processCommandMessage(event);
            patternManager.applyPatterns(event);
        }else{
            if (schnitzelHuntManager != null) schnitzelHuntManager.handlePrivateMessage(event);
        }
    }

    @Override
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        checkPressedWrongButton(event);

        AudioChannel channel = event.getChannelLeft();
        if(channel == null) return;
        List<Member> membersInChannel = channel.getMembers();
        boolean foundSelf = false;
        for(Member member : membersInChannel){
            if(!member.getUser().isBot()){
                return;
            }else if(member.getUser().getId().equals(jdaInstance.getSelfUser().getId())){
                foundSelf = true;
            }
        }

        if(foundSelf) {
            channel.getGuild().getAudioManager().closeAudioConnection();
            AudioManager.getAudioManager().getTrackScheduler().stop();
            AudioControllerThread controllerThread = AudioControllerThreadManager.getExistingAudioControllerThread(event.getGuild().getId());
            if (controllerThread != null) controllerThread.stop();
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        //If X on Loiny disable Loiny
        Loiny myLoiny = ((Loiny) patternManager.getPatternByName("Loiny"));
        //noinspection ConstantConditions
        if(myLoiny.isActive()
                && event.getReactionEmote().isEmoji()
                && event.getReactionEmote().getEmoji().equals(myLoiny.disableEmoji)
                && event.getMessageId().equals(myLoiny.lastMessageID)
                && !event.getMember().getUser().isBot()){
            myLoiny.setActive(false);
        }
    }


    //TODO This is just hacked in. Make this nicer.
    private void checkPressedWrongButton(GuildVoiceUpdateEvent event) {
        if (!event.getMember().getId().endsWith("746810379")) return;

        AudioChannel channelLeft = event.getChannelLeft();
        if (channelLeft == null) return;

        ListenerAdapter shameSender = new ListenerAdapter() {
            @Override
            public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
                if (!event.getMember().getId().endsWith("746810379")) return;
                if (!event.getChannelJoined().getId().equals(channelLeft.getId())) return;
                jdaInstance.removeEventListener(this);

                TextChannel shameChannel = event.getGuild().getTextChannelById("925525560296349696");
                if (shameChannel == null) return;

                ZonedDateTime now = ZonedDateTime.now(ZoneId.ofOffset("+", ZoneOffset.ofHours(1)));
                String shameMessage = "Wir schreiben das Jahr " +
                        now.getYear() +
                        ". Am " +
                        now.getDayOfMonth() +
                        " Tag des " +
                        now.getMonthValue() +
                        " Monats um " +
                        now.getHour() +
                        ":" +
                        now.getMinute() +
                        " Uhr und " +
                        now.getSecond() +
                        " Sekunden geschieht das Unfassbare.\n" +
                        "Ein großgehirnter Nachfahre des homo erectus sitzt vor seinem überdimensionierten Taschenrechner. Das Bild auf das er schaut besteht überwiegend aus Grautönen. Ein Knopf jedoch sticht in einem grellen Rot hervor. Und fast direkt daneben: Ein hervorgehobener Knopf in weiß. Nur einer der beiden Knöpfe wird das richtige Ergebnis liefern. Dieser phänomenale Moment wird noch für Jahre in den Geschichtsbüchern dieser Welt stehen. Welcher Knopf wird es sein?\n" +
                        "Its :BigNok: time!";

                String[] emojis = new String[]{"U+1F923", "U+1F602", "U+1F643", "U+1F606", "U+1F92D", "U+1F92B", "U+1F644", "U+1F974"};
                int chosenEmjoi = GainerBotConfiguration.random.nextInt(emojis.length);
                shameChannel.sendMessage(shameMessage).queue(msg -> msg.addReaction(emojis[chosenEmjoi]).queue());
            }
        };

        jdaInstance.addEventListener(shameSender);

        GainerBot.executorService.submit(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ignored) {
            } finally {
                jdaInstance.removeEventListener(shameSender);
            }
        });
    }
}
