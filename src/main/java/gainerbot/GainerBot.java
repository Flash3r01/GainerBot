package gainerbot;

import gainerbot.audio.AudioManager;
import gainerbot.patterns.Loiny;
import gainerbot.schnitzel.SchnitzelHuntManager;
import gainerbot.services.HttpService;
import gainerbot.slashCommand.SlashCommandManager;
import gainerbot.slashCommand.commands.*;
import gainerbot.slashCommand.commands.audio.Play;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
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
        SlashCommandManager slashManager = new SlashCommandManager();
        if (!GainerBotConfiguration.isDebug){
            slashManager.addCommand(new B());
            slashManager.addCommand(new Fuck());
            slashManager.addCommand(new Google());
            slashManager.addCommand(new Help());
            slashManager.addCommand(new OwO());
            slashManager.addCommand(new Pasta());
            slashManager.addCommand(new Pattern());
            slashManager.addCommand(new Play());
            slashManager.addCommand(new Status());
            slashManager.addCommand(new Stonks());
            slashManager.addCommand(new Surprise());
            slashManager.addCommand(new Watch2Gether());
            slashManager.registerGlobalCommands(jdaInstance);
        }
        else{
            //slashManager.addCommand(new Test());
            slashManager.addCommand(new B());
            slashManager.addCommand(new Fuck());
            slashManager.addCommand(new Google());
            slashManager.addCommand(new Help());
            slashManager.addCommand(new OwO());
            slashManager.addCommand(new Pasta());
            slashManager.addCommand(new Pattern());
            slashManager.addCommand(new Play());
            slashManager.addCommand(new Status());
            slashManager.addCommand(new Stonks());
            slashManager.addCommand(new Surprise());
            slashManager.addCommand(new Watch2Gether());
            slashManager.registerGuildCommands(jdaInstance.getGuildById(GainerBotConfiguration.debugGuildId));
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
        //TODO Is this necessary?
        super.onGuildVoiceUpdate(event);

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
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        //TODO Is this necessary?
        super.onMessageReactionAdd(event);

        //If X on Loiny disable Loiny
        Loiny myLoiny = ((Loiny) patternManager.getPatternByName("Loiny"));
        if(myLoiny.isActive()
                && event.getReactionEmote().isEmoji()
                && event.getReactionEmote().getEmoji().equals(myLoiny.disableEmoji)
                && event.getMessageId().equals(myLoiny.lastMessageID)){
            myLoiny.setActive(false);
        }
    }
}
