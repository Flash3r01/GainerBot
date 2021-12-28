package gainerbot.slashCommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class BaseSlashCommand extends ListenerAdapter {
    protected final CommandData commandData;
    private final List<Predicate<SlashCommandEvent>> commandConditions = new ArrayList<>();

    protected BaseSlashCommand(CommandData commandData) {
        this.commandData = commandData;
    }

    protected abstract void execute(@Nonnull SlashCommandEvent event);

    public void addCondition(Predicate<SlashCommandEvent> condition){
        if (condition != null && !commandConditions.contains(condition))
            commandConditions.add(condition);
    }

    public CommandData getCommandData(){
        return commandData;
    }

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equals(this.getCommandData().getName())) return;
        boolean conditionsMet = commandConditions.stream().allMatch(predicate -> predicate.test(event));
        if (!conditionsMet) return;

        this.execute(event);

        if (!event.isAcknowledged()){
            event.reply("Oh no!\nMy developer seems to not know what he is doing... Feel free to tell him to fix the /"+this.getCommandData().getName()+" command ;).")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
