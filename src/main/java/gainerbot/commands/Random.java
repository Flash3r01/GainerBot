package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

public class Random extends BaseCommand {
    public Random() {
        super(new String[]{"random", "rnd"}, "Generates a random number between 0(inclusive) and 1(exclusive)");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        event.getChannel().sendMessage("Your random Number is: " + GainerBotConfiguration.random.nextFloat()).queue();
    }
}
