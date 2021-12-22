package gainerbot.slashCommand.commands;

import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.util.List;

public class Watch2Gether extends BaseSlashCommand {
    private static final String w2gUrl = "https://w2g.tv/31vo17j124wmmaf9xp";

    public Watch2Gether() {
        super(new CommandData("watch2gether", "Sends the Link to our Watch2Gether-Room"));
    }


    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        Guild guild = event.getGuild();

        if (guild == null){
            event.reply("Klick me: " + w2gUrl).queue();
            return;
        }

        event.deferReply(true).queue();
        TextChannel foundw2gChannel = getW2GChannel(guild.getTextChannels());
        if (foundw2gChannel == null) {
            event.getHook().sendMessage("Klick me: " + w2gUrl).queue();
            return;
        }

        foundw2gChannel.sendMessage("Klick me: " + w2gUrl).queue();
        event.getHook().sendMessage("The link has been posted in "+foundw2gChannel.getAsMention()).queue();
    }

    public TextChannel getW2GChannel(List<TextChannel> textChannels){
        for(TextChannel textChannel : textChannels){
            if(textChannel.getName().toLowerCase().contains("watch2gether")){
                return textChannel;
            }
        }
        return null;
    }
}
