package gainerbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

public class GainerBot extends ListenerAdapter {
    public static JDA jdaInstance;

    public static final GainerBotCommands commandManager = new GainerBotCommands();
    public static final GainerBotPatterns patternManager = new GainerBotPatterns();

    public static final HttpService httpService = new HttpService();

    public static void startGainerBot(){
        if(jdaInstance != null){
            System.out.println("There is already a GainerBot running.");
            return;
        }

        //TODO read all the configuration at once.
        String token = "";
        try {
            Path path = Paths.get(GainerBotConfiguration.basePath.toString(), "token.txt");
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
        for(Guild guild : jdaInstance.getGuilds()){
            guild.loadMembers().onSuccess(list -> System.out.println("Members loaded"));
        }
        System.out.println("GainerBot started.");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        commandManager.processCommandMessage(event);
        patternManager.applyPatterns(event);
    }

    @Override
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        //Is this necessary?
        super.onGuildVoiceUpdate(event);

        VoiceChannel channel = event.getChannelLeft();
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

        if(foundSelf) channel.getGuild().getAudioManager().closeAudioConnection();
    }
}
