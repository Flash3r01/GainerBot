package gainerbot;

import gainerbot.commands.*;
import gainerbot.commands.audio.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GainerBotCommands {
    private String prefix = GainerBotConfiguration.prefix;

    private final List<BaseCommand> commands = new ArrayList<>();
    private final List<String[]> commandNames = new ArrayList<>();

    public GainerBotCommands(){
        //Here come all the commands that the Bot knows
        commands.add(new Random());
        commands.add(new Stonks());
        commands.add(new Help());
        commands.add(new Test());
        commands.add(new Google());
        commands.add(new Watch2Gether());
        commands.add(new Nibba());
        commands.add(new Fuck());
        commands.add(new Pasta());
        commands.add(new OWOifier());
        commands.add(new Pattern());
        commands.add(new Sound());
        commands.add(new Surprise());
        commands.add(new Pog());
        commands.add(new Status());
        commands.add(new Play());
        commands.add(new Skip());
        commands.add(new Stop());
        commands.add(new Pause());
        commands.add(new Resume());

        //Extract all the commandNames.
        for(BaseCommand command : commands){
            commandNames.add(command.names);
        }
    }


    public boolean processCommandMessage(@Nonnull MessageReceivedEvent event){
        String msg = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        if(!msg.startsWith(prefix)) return false;
        msg = msg.substring(prefix.length());

        CommandRepresentation cmd = CommandRepresentation.commandFromTokens(commandToTokens(msg));
        if(cmd == null) {
            showCommandHelpMessage(event.getChannel());
            return false;
        }

        int currentCommandIndex = 0;
        for(String[] names : commandNames){
            for(String name : names){
                if(cmd.command.equalsIgnoreCase(name)){
                    BaseCommand command = commands.get(currentCommandIndex);

                    if(command.listensOnChannel(channel.getName())) {
                        command.execute(event, cmd.options, cmd.arguments);
                        return true;
                    }else return false;

                }
            }
            currentCommandIndex++;
        }

        return false;
    }

    private void showCommandHelpMessage(MessageChannel channel) {
        String msg = "Your command is not structured correctly!\nAll commands to the GainerBot have to follow this structure:\n`" +
                prefix + "<command> [-<optionIdentifier>=<value>]... [argument]...`";
        channel.sendMessage(msg).queue();
    }

    private String[] commandToTokens(String msg){
        ArrayList<String> tokens = new ArrayList<>();
        char[] charMsg = msg.toCharArray();

        int currentCharIndex = 0;
        while(currentCharIndex < charMsg.length) {

            StringBuilder nextToken = new StringBuilder();
            boolean inString = false;
            boolean escapeNext = false;

            for (; currentCharIndex < charMsg.length; currentCharIndex++) {

                if (escapeNext) {
                    nextToken.append(charMsg[currentCharIndex]);
                    escapeNext = false;
                    continue;
                }

                switch (charMsg[currentCharIndex]) {
                    case '\\':
                        escapeNext = true;
                        continue;
                    case '"':
                        inString = !inString;
                        continue;
                    case ' ':
                    case '\t':
                        if(inString){
                            nextToken.append(charMsg[currentCharIndex]);
                            continue;
                        }else break;
                    default:
                        nextToken.append(charMsg[currentCharIndex]);
                        continue;
                }
                currentCharIndex++;
                break;
            }
            if(nextToken.length() > 0) tokens.add(nextToken.toString());
        }

        return tokens.toArray(new String[0]);
    }


    private static class CommandRepresentation{
        private final String command;
        private final HashMap<String, String> options;
        private final String[] arguments;

        private CommandRepresentation(String command){
            this.command = command;
            this.options = new HashMap<>();
            this.arguments = new String[0];
        }

        public CommandRepresentation(String command, HashMap<String, String> options, String[] arguments) {
            this.command = command;
            this.options = options;
            this.arguments = arguments;
        }

        private static CommandRepresentation commandFromTokens(String[] tokens){
            switch(tokens.length){
                case 0:
                    return null;
                case 1:
                    return new CommandRepresentation(tokens[0]);
                default:
            }

            String cmd = tokens[0];
            HashMap<String, String> ops = new HashMap<>();

            //Next try
            int argumentStart = -1;
            for(int i = 1; i < tokens.length; i++){
                String currentToken = tokens[i];
                if(currentToken.startsWith("-")){
                    //Is - or --
                    int tokensConsumed = parseOption(ops, i, tokens);
                    if(tokensConsumed == -1) return null;
                    i += tokensConsumed-1;
                }else{
                    //Is argument
                    argumentStart = i;
                    break;
                }
            }

            ArrayList<String> args = new ArrayList<>();
            if(argumentStart != -1){
                args = new ArrayList<>(Arrays.asList(tokens).subList(argumentStart, tokens.length));
            }

            return new CommandRepresentation(cmd, ops, args.toArray(new String[0]));
        }

        private static int parseOption(HashMap<String, String> outputMap, int optionIndex, String[] tokens){
            String firstToken = tokens[optionIndex];

            if(firstToken.startsWith("--")){
                //We know it starts with 2 -
                if(firstToken.contains("=") || (optionIndex+1<tokens.length && tokens[optionIndex+1].startsWith("="))){
                    return parseOptionWithValue(outputMap, optionIndex, tokens);
                }else{
                    outputMap.put(firstToken.substring(2), "");
                }
            }else{
                //We know it starts with only 1 -
                if(firstToken.contains("=") || (optionIndex+1<tokens.length && tokens[optionIndex+1].startsWith("="))){
                    return parseOptionWithValue(outputMap, optionIndex, tokens);
                }else{
                    for(int i = 1; i < firstToken.length(); i++){
                        outputMap.put(""+firstToken.charAt(i), "");
                    }
                }
            }
            return 1;
        }

        private static int parseOptionWithValue(HashMap<String, String> outputMap, int optionIndex, String[] tokens){
            String optionIdentifier = tokens[optionIndex];
            String optionValue;
            int ret;

            //Remove '-'es
            while(optionIdentifier.charAt(0) == '-'){
                optionIdentifier = optionIdentifier.substring(1);
            }

            if(optionIdentifier.contains("=")){
                optionIdentifier = optionIdentifier.substring(0, optionIdentifier.indexOf("="));
                if(!tokens[optionIndex].endsWith("=")){
                    optionValue = tokens[optionIndex];
                    optionValue = optionValue.substring(optionValue.indexOf('=')+1);
                    ret = 1;
                }else if(optionIndex+1 < tokens.length){
                    optionValue = tokens[optionIndex+1];
                    ret = 2;
                }else return -1;
            }else{
                if(optionIndex+1 < tokens.length){
                    if(tokens[optionIndex+1].equals("=")){
                        if(optionIndex+2 < tokens.length){
                            optionValue = tokens[optionIndex+2];
                            ret = 3;
                        }else return -1;
                    }else{
                        optionValue = tokens[optionIndex+1].substring(1);
                        ret = 2;
                    }
                }else return -1;
            }

            outputMap.put(optionIdentifier, optionValue);
            return ret;
        }
    }

    //region Getter and Setter
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<BaseCommand> getCommands() {
        return commands;
    }

    public List<String[]> getCommandNames() {
        return commandNames;
    }

    public BaseCommand getCommandByName(String name){
        for(int i = 0; i < commandNames.size(); i++){
            for(int j = 0; j< commandNames.get(i).length; j++){
                if(name.equalsIgnoreCase(commandNames.get(i)[j])){
                    return commands.get(i);
                }
            }
        }
        return null;
    }
    //endregion
}
