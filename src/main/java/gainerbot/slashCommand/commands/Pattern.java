package gainerbot.slashCommand.commands;

import gainerbot.GainerBot;
import gainerbot.patterns.BasePattern;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Pattern extends BaseSlashCommand {
    public Pattern() {
        //TODO The Options here could be expanded/This command could be made more modern. Also, you used to be able to set AND toggle.
        super(new CommandData("pattern", "Shows and toggles patterns")
                .addOption(OptionType.STRING, "name", "Name of pattern to toggle", false));
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply(true).queue();
        OptionMapping nameOption = event.getOption("name");
        String name = nameOption != null ? nameOption.getAsString() : null;

        // Handle no options given.
        if (name == null) {
            event.getHook().sendMessage(createStatusString()).queue();
        }
        // Handle options given.
        else{
            BasePattern patternToToggle = getPatternByName(name);
            if (patternToToggle == null){
                event.getHook().sendMessage("There is no pattern with the name \""+name+"\".").queue();
                return;
            }

            patternToToggle.setActive(!patternToToggle.isActive());
            event.getHook().sendMessage("Pattern \""+name+"\" is now "+(patternToToggle.isActive()?"on":"off")+".").queue();
        }
    }

    private String createStatusString(){
        StringBuilder builder = new StringBuilder();
        builder.append("```\nPattern Status:\n");
        for(BasePattern pattern : GainerBot.patternManager.getPatterns()){
            builder.append(pattern.getName());
            builder.append(" ".repeat(Math.max(12-pattern.getName().length(), 0)));
            builder.append("-    ");
            builder.append(pattern.isActive()?"On\n":"Off\n");
        }
        builder.append("```");

        return builder.toString();
    }

    private BasePattern getPatternByName(String name){
        for(BasePattern pattern : GainerBot.patternManager.getPatterns()){
            if(name.equalsIgnoreCase(pattern.getName())){
                return pattern;
            }
        }
        return null;
    }
}
