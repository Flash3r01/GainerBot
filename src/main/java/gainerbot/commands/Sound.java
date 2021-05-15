package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.audio.AudioPlayerSendHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Sound extends BaseCommand {
    private final Path soundBase;

    private String[] soundNames;

    public Sound() {
        super(new String[] {"sound"}, "Plays a sound in your current voice channel.");
        soundBase = GainerBotConfiguration.basePath.resolve("sound");
        initSound();
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        Member member = event.getMember();
        if(member != null){
            GuildVoiceState voiceState = member.getVoiceState();

            if(voiceState != null && voiceState.inVoiceChannel()){
                if(arguments.length < 1){
                    showAvailableSounds(event.getChannel());
                    event.getMessage().delete().queue();
                    return;
                }
                String soundPath = optionsToSoundPath(arguments);
                if(soundPath == null){
                    event.getChannel().sendMessage("Sound \""+arguments[0]+"\" has not been found.").queue();
                    return;
                }
                event.getMessage().delete().queue();
                gainerbot.audio.AudioManager.getAudioManager().loadAudio(soundPath, event.getChannel());
                connectToChannel(voiceState.getChannel());
            }else{
                event.getChannel().sendMessage("You have to be in a Voice Channel to use this command.").queue();
            }
        }else{
            event.getChannel().sendMessage("You have to use this command in a Server.").queue();
        }
    }

    private void showAvailableSounds(MessageChannel channel){
        StringBuilder builder = new StringBuilder();

        builder.append("__Following sounds are currently available:__ :loud_sound:\n");
        for(String name : soundNames){
            builder.append(name, 0, name.lastIndexOf('.'));
            builder.append("\n");
        }
        channel.sendMessage(builder.toString()).queue();
    }

    private void connectToChannel(VoiceChannel channel){
        if(channel == null) return;

        AudioManager audioManager = channel.getGuild().getAudioManager();
        AudioPlayerSendHandler sendHandler = new AudioPlayerSendHandler(gainerbot.audio.AudioManager.getAudioManager().getPlayer());

        audioManager.setSendingHandler(sendHandler);
        audioManager.openAudioConnection(channel);
    }

    private String optionsToSoundPath(String[] options){
        String ret = null;
        if(options.length >= 1){
            for(String name : soundNames){
                //TODO Fix the startsWith comparison.
                if(name.toLowerCase().startsWith(options[0].toLowerCase())){
                    ret = soundBase.resolve(name).toString();
                }
            }
        }
        return ret;
    }

    private void initSound(){
        ArrayList<String> names = new ArrayList<>();
        File[] files = soundBase.toFile().listFiles();
        if(files == null){
            soundNames = new String[0];
            return;
        }
        for(File file : files){
            String name = file.toPath().getFileName().toString();
            if(name.endsWith(".md")) continue;
            names.add(name);
        }
        soundNames = names.toArray(new String[0]);
    }
}
