package gainerbot.slashCommand;

import gainerbot.GainerBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;

public class CollectingState extends SlashCommandManagerState {
    public CollectingState(SlashCommandManager manager) {
        super(manager);
    }

    @Override
    public boolean addCommand(BaseSlashCommand toAdd) {
        if (SlashCommandManager.getRegisteredNames().contains(toAdd.getCommandData().getName())){
            System.out.println("WARNING: The command /"+toAdd.getCommandData().getName()+" has already been registered! Skipping");
            return false;
        }
        return manager.getCommandMap().putIfAbsent(toAdd.getCommandData().getName(), toAdd) == null;
    }

    @Override
    public boolean registerGlobalCommands(JDA jdaInstance) {
        List<CommandData> commandDatas = registerListenersAndReturnCommandData();
        jdaInstance.updateCommands().addCommands(commandDatas).queue();
        manager.setState(new RegisteredState(manager));
        return true;
    }

    @Override
    public boolean registerGuildCommands(Guild guild) {
        List<CommandData> commandDatas = registerListenersAndReturnCommandData();
        guild.updateCommands().addCommands(commandDatas).queue();
        manager.setState(new RegisteredState(manager));
        return true;
    }

    private List<CommandData> registerListenersAndReturnCommandData(){
        List<CommandData> commandDatas = new ArrayList<>();
        manager.getCommandMap().forEach((key, value) -> {
            if (SlashCommandManager.getRegisteredNames().contains(value.getCommandData().getName())){
                System.out.println("WARNING: The command "+value.getCommandData().getName()+" has already been registered! Skipping");
                return;
            }
            commandDatas.add(value.getCommandData());
            GainerBot.jdaInstance.addEventListener(value);
        });
        return commandDatas;
    }
}
