package gainerbot.slashCommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public abstract class BaseSlashCommand extends ListenerAdapter {
    protected final CommandData commandData;

    protected BaseSlashCommand(CommandData commandData) {
        this.commandData = commandData;
    }

    protected abstract void execute(@Nonnull SlashCommandEvent event);

    public CommandData getCommandData(){
        return commandData;
    }

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equals(this.getCommandData().getName())) return;

        this.execute(event);

        if (!event.isAcknowledged()){
            event.reply("Oh no!\nMy developer seems to not know what he is doing... Feel free to tell him to fix the /"+this.getCommandData().getName()+" command ;).")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
