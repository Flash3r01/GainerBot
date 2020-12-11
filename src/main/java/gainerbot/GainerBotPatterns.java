package gainerbot;

import gainerbot.patterns.BasePattern;
import gainerbot.patterns.Game;
import gainerbot.patterns.Loiny;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class GainerBotPatterns {
    private final ArrayList<BasePattern> patterns = new ArrayList<>();

    public GainerBotPatterns(){
        //Add all the Patterns the Bot should know.
        patterns.add(new Game());
        patterns.add(new Loiny());
    }

    public void applyPatterns(@Nonnull MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;

        for(BasePattern pattern : patterns){
            if(pattern.isActive() && pattern.listensOnChannel(event.getChannel())){
                pattern.run(event);
            }
        }
    }

    public ArrayList<BasePattern> getPatterns() {
        return patterns;
    }
}
