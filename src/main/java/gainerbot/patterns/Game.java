package gainerbot.patterns;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Game extends BasePattern {
    public Game() {
        super("Game", "Censors the rudest word in the world.");
        this.listeningChannels = IChannelPermission.Presets.getElevatedPermissions();
    }

    @Override
    public void run(@Nonnull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().toLowerCase().contains("game")){
            if(event.getMessage().getContentRaw().toLowerCase().contains("http")){
                event.getChannel().sendMessage("Watch out! You probably used a rude word in a Link. Please be more careful next time!").queue();
                return;
            }
            String newMsg = event.getAuthor().getAsMention() +
                    "\nOy, retard! Take this piece of :soap: and wash your fucking mouth!\n" +
                    "And never mention this godforsaken word again on this server.\n" +
                    "I have been nice and censored your Message for you:\n\n> " +
                    event.getMessage().getContentRaw().replace("game", "g\\*me")
                            .replace("Game", "G\\*me")
                            .replace("\n", "\n> ");

            event.getMessage().delete().queue();
            event.getChannel().sendMessage(newMsg).queue();
        }
    }
}
