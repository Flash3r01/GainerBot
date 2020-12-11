package gainerbot.patterns;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Loiny extends BasePattern{

    public Loiny() {
        super("Loiny", "Gives Lenny the attention he deserves.");
    }

    @Override
    public void run(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().getId().equals("271344495885418497")){
            String msg = event.getAuthor().getAsMention() +
                    "\n🤣🤣Oh mi **Goooosh**!🤩 Look at yor naim!😏😏 It remembered me of dis here‼‼‼ Luuk at it xD 🙃" +
                    "\nhttps://youtu.be/YPB-hpNgQNI?t=49";
            event.getMessage().reply(msg).queue();
        }
    }
}
