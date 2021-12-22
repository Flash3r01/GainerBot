package gainerbot.slashCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class RegisteredState extends SlashCommandManagerState {
    public RegisteredState(SlashCommandManager manager) {
        super(manager);
    }

    @Override
    public boolean addCommand(BaseSlashCommand toAdd) {
        System.out.println("WARNING: Tried to add commands to a command manager that has already registered its commands! Skipping.");
        return false;
    }

    @Override
    public boolean registerGlobalCommands(JDA jdaInstance) {
        System.out.println("WARNING: Tried to register commands of a command manager twice! Skipping.");
        return false;
    }

    @Override
    public boolean registerGuildCommands(Guild guild) {
        System.out.println("WARNING: Tried to register commands of a command manager twice! Skipping.");
        return false;
    }
}
