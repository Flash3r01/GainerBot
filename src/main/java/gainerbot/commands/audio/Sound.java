package gainerbot.commands.audio;

import gainerbot.GainerBotConfiguration;
import gainerbot.audio.AudioHelper;
import gainerbot.audio.AudioManager;
import gainerbot.commands.BaseCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
        if(AudioHelper.checkAudioRequirementsAndNotify(event.getMember(), event.getChannel())){
            if(arguments.length < 1){
                showAvailableSounds(event.getChannel());
                event.getMessage().delete().queue();
                return;
            }
            String soundPath = optionsToSoundPath(arguments);
            if(soundPath == null){
                event.getChannel().sendMessage("Sounds have to be requested one at a time :(.").queue();
                return;
            }
            else if(soundPath.equals("")){
                event.getChannel().sendMessage("Sound \""+arguments[0]+"\" has not been found.").queue();
                return;
            }

            gainerbot.audio.AudioManager.getAudioManager().loadAudio(soundPath, event.getChannel(), true);
            //noinspection ConstantConditions
            AudioHelper.connectToChannel(event.getMember().getVoiceState().getChannel(), AudioManager.getAudioManager().getPlayer());
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

    /**
     * Parses options to a path to the requested soundfile.
     * @param options The name of the file to play. Should only contain one name.
     * @return The path to the requested soundfile as a string. Empty String if file not found. Null if given array has more than one element.
     */
    private String optionsToSoundPath(String[] options){
        if(options.length == 1){
            for(String name : soundNames){
                //TODO Fix the startsWith comparison.
                if(name.toLowerCase().startsWith(options[0].toLowerCase())){
                    Path filePath = soundBase.resolve(name);
                    return filePath.toFile().exists() ? filePath.toString() : "";
                }
            }
        }
        return null;
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
