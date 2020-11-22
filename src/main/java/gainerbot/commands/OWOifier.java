package gainerbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class OWOifier extends BaseCommand{
    public OWOifier() {
        super(new String[]{"owo", "OwO", "OWO"}, "UwU");
    }

    @Override
    public void execute(@NotNull MessageReceivedEvent event, String[] options) {
        if(options.length > 0){
            String s = options[0].replace("r", "w").replace("R","W").replace("l","w").replace("L","W");
            event.getChannel().sendMessage(s).queue();
        }else {
            event.getChannel().sendMessage("UwU gimme text").queue();
        }
    }
}
