package gainerbot.commands;

import gainerbot.GainerBot;
import gainerbot.patterns.BasePattern;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Pattern extends BaseCommand {
    public Pattern() {
        super(new String[]{"pattern", "patterns"}, "Shows all loaded patterns, or toggles them.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        switch(options.length){
            case 1:
                togglePattern(event, options[0]);
                break;
            case 2:
                setPattern(event, options[0], options[1]);
                break;
            case 0:
            default:
                event.getChannel().sendMessage(createStatusString()).queue();
                break;
        }
    }

    private String createStatusString(){
        StringBuilder builder = new StringBuilder();
        builder.append("```\nPattern Status:\n");
        for(BasePattern pattern : GainerBot.patternManager.getPatterns()){
            builder.append(pattern.getName());
            builder.append(" ".repeat(12-pattern.getName().length()));
            builder.append("-    ");
            builder.append(pattern.isActive()?"On\n":"Off\n");
        }
        builder.append("```");

        return builder.toString();
    }

    private void setPattern(@Nonnull MessageReceivedEvent event, String patternName, String value){
        BasePattern pattern;
        if((pattern = getPatternWithName(patternName)) != null){
            if(value.equalsIgnoreCase("on")){
                pattern.setActive(true);
                event.getChannel().sendMessage("Pattern: " + pattern.getName() + " is now on.").queue();
            }else if(value.equalsIgnoreCase("off")){
                pattern.setActive(false);
                event.getChannel().sendMessage("Pattern: " + pattern.getName() + " is now off.").queue();
            }else{
                event.getChannel().sendMessage("I only understand the values on/off.\nOr just name the Pattern to toggle it.").queue();
            }
        }else{
            event.getChannel().sendMessage("I can not find a pattern with that name.").queue();
        }
    }

    private void togglePattern(@Nonnull MessageReceivedEvent event, String patternName){
        BasePattern pattern;
        if((pattern = getPatternWithName(patternName)) != null){
            pattern.setActive(!pattern.isActive());
            event.getChannel().sendMessage("Pattern: " + pattern.getName() + " is now " + (pattern.isActive()?"on.":"off.")).queue();
        }else{
            event.getChannel().sendMessage("I can not find a pattern with that name.").queue();
        }
    }

    private BasePattern getPatternWithName(String name){
        for(BasePattern pattern : GainerBot.patternManager.getPatterns()){
            if(name.equalsIgnoreCase(pattern.getName())){
                return pattern;
            }
        }
        return null;
    }
}
