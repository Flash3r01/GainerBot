package gainerbot.commands;

import gainerbot.GainerBotConfiguration;
import gainerbot.permissions.IChannelPermission;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Random extends BaseCommand {
    public Random() {
        super(new String[]{"random", "rnd"}, "Generates a random number between 0(inclusive) and 1(exclusive)");
        this.listeningChannels = IChannelPermission.Presets.getChatPermissions();
    }

    //TODO Rework all of this...
    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        java.util.Random r = GainerBotConfiguration.random;
        if(arguments.length == 0){
            event.getChannel().sendMessage("Your random Number is: " + r.nextFloat()).queue();
        }
        else if(arguments.length == 1){
            try {
                int randomInt = r.nextInt(Integer.parseInt(arguments[0]));
                event.getChannel().sendMessage("Your random number is: " + randomInt).queue();
            } catch (NumberFormatException e) {

                if(arguments[0].equalsIgnoreCase("online")){
                    //TODO Test if this works - online should not work :/
                    List<Member> onlineMembers = event.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).collect(Collectors.toList());
                    int randomMemberIndex = r.nextInt(onlineMembers.size());
                    event.getChannel().sendMessage("Congratz! " + onlineMembers.get(randomMemberIndex).getAsMention() + " was chosen randomly.").queue();
                }
                else if(arguments[0].equalsIgnoreCase("voice")){
                    Member member = event.getMember();
                    if(member != null){
                        //TODO Test if uncached members are also included.
                        AudioChannel channel = member.getVoiceState().getChannel();
                        if(channel != null){
                            List<Member> members = channel.getMembers();
                            event.getChannel().sendMessage("Congratz! " + members.get(r.nextInt(members.size())).getAsMention() + " was chosen randomly.").queue();
                        }else{
                            event.getChannel().sendMessage("You have to be in a voice channel to use that command.").queue();
                        }
                    }
                }else {
                    randomFromSet(event, arguments);
                    /*
                    int charIndex = r.nextInt(options[0].length());
                    event.getChannel().sendMessage("Your random char from the string is: " + options[0].charAt(charIndex)).queue();
                     */
                }
            }
        }
        else{
            if(arguments[0].equalsIgnoreCase("oneof")){
                int randomIndex = r.nextInt(arguments.length-1)+1;
                event.getChannel().sendMessage("The chosen option is: " + arguments[randomIndex]).queue();
            }
            else{
                randomFromSet(event, arguments);
            }
        }
    }

    //TODO Make the online part of it work.
    private void randomFromSet(@Nonnull MessageReceivedEvent event, String[] options){
        HashSet<String> pool = new HashSet<>();

        //Add all literals.
        for (String option : options) {
            if (!option.startsWith("<@") && !option.equalsIgnoreCase("online")) {
                pool.add(option);
            }
        }
        //Get all mentioned members.
        List<String> members;
        if(options[0].equalsIgnoreCase("online")){
            members = event.getMessage().getMentionedMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).map(IMentionable::getAsMention).collect(Collectors.toList());
        }else{
            members = event.getMessage().getMentionedMembers().stream().map(IMentionable::getAsMention).collect(Collectors.toList());
        }
        //Get all mentioned roles' members.
        List<Role> roles = event.getMessage().getMentionedRoles();
        HashSet<String> mentionedRoleMembers = new HashSet<>();
        if(options[0].equalsIgnoreCase("online")){
            for(Role role : roles){
                //List<Member> test = event.getGuild().getMembersWithRoles(role);
                mentionedRoleMembers.addAll(event.getGuild().getMembersWithRoles(role).stream()
                        .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                        .map(IMentionable::getAsMention)
                        .collect(Collectors.toList()));
            }
        }else{
            for(Role role : roles){
                mentionedRoleMembers.addAll(event.getGuild().getMembersWithRoles(role).stream()
                        .map(IMentionable::getAsMention)
                        .collect(Collectors.toList()));
            }
        }

        pool.addAll(members);
        pool.addAll(mentionedRoleMembers);
        if(pool.size() <= 0) {
            event.getChannel().sendMessage("No Element matched the criteria, so nothing was chosen.").queue();
            return;
        }

        String chosenElement = "";
        int counter = 0;
        int goal = GainerBotConfiguration.random.nextInt(pool.size());
        for(String element : pool){
            if(counter == goal){
                chosenElement = element;
                break;
            }
            counter++;
        }

        event.getChannel().sendMessage("The omniscient GainerBot chooses: " + chosenElement).queue();
    }
}
