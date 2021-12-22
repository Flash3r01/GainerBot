package gainerbot.slashCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public abstract class SlashCommandManagerState {
    protected SlashCommandManager manager;

    public abstract boolean addCommand(BaseSlashCommand toAdd);
    public abstract boolean registerGlobalCommands(JDA jdaInstance);
    public abstract boolean registerGuildCommands(Guild guild);

    public SlashCommandManagerState(SlashCommandManager manager) {
        this.manager = manager;
    }
}
