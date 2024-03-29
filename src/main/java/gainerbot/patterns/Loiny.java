package gainerbot.patterns;

import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Loiny extends BasePattern{
    public final String loinyID = "271344495885418497";
    public final String disableEmoji = "\u274C";

    public String lastMessageID = "";

    public Loiny() {
        super("Loiny", "Gives Lenny the attention he deserves.");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
        isActive = false;
    }

    @Override
    public void run(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().getId().equals(loinyID)){
            String msg = event.getAuthor().getAsMention() +
                    "\n:rofl::rofl:Oh mi **Goooosh**!:heart_eyes: Look at yor naim!:exploding_head: It remembered me of dis here:exclamation::exclamation::smirk: Luuk at it xD :upside_down:" +
                    "\nhttps://youtu.be/YPB-hpNgQNI?t=49";
            event.getMessage().reply(msg).queue(newMsg -> {
                newMsg.addReaction(disableEmoji).queue();
                lastMessageID = newMsg.getId();
            });
        }
    }
}
