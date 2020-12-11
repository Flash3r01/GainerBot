package gainerbot.commands;

import gainerbot.GainerBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class Help extends BaseCommand{
    private static String helpString;

    public Help() {
        super(new String[]{"help", "?"}, "Lists all commands and a short description of them");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, String[] options) {
        event.getChannel().sendMessage(getHelpString()).queue();
    }

    public static String getHelpString(){
        if(helpString == null) {
            List<BaseCommand> commands = GainerBot.commandManager.getCommands();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("```\n");

            for (BaseCommand command : commands) {
                String commandString = GainerBot.commandManager.getPrefix() + command.names[0];
                commandString += " ".repeat(15-commandString.length());
                commandString += "-\t";
                commandString += command.description + "\n";

                stringBuilder.append(commandString);
            }

            stringBuilder.append("```");

            helpString = stringBuilder.toString();
        }

        return Help.helpString;
    }
}
