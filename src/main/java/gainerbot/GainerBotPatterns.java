package gainerbot;

import gainerbot.patterns.BasePattern;
import gainerbot.patterns.Game;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class GainerBotPatterns {
    private final ArrayList<BasePattern> patterns = new ArrayList<>();

    public GainerBotPatterns(){
        //Add all the Patterns the Bot should know.
        patterns.add(new Game());
    }

    public void applyPatterns(@Nonnull MessageReceivedEvent event){
        for(BasePattern pattern : patterns){
            if(pattern.isActive() && pattern.listensOnChannel(event.getChannel())){
                pattern.run(event);
            }
        }
    }
}
