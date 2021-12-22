package gainerbot.slashCommand.commands;

import gainerbot.services.SystemInfo;
import gainerbot.slashCommand.BaseSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public class Status extends BaseSlashCommand {
    public Status() {
        super(new CommandData("status", "Shows a bunch of information about the machine the bot is running on."));
        //TODO This command should only be usable by trusted Users.
    }

    @Override
    protected void execute(@Nonnull SlashCommandEvent event) {
        event.deferReply(true).queue();

        event.getHook().sendMessage(systemInfoToString(SystemInfo.get())).queue();
    }

    private String systemInfoToString(SystemInfo info){
        return "**Gainerbot is running on a:** " + SystemInfo.getArchitecture() + " " + SystemInfo.getOsName() + "\n\n" +
                "**CPU Load:** " + info.getCachedCpuLoad() + "\n" +
                "**Memory (System):** " + info.getCachedSystemRamUsed() + " / " + info.getCachedSystemRamMax() + "\n" +
                "**Memory (Java):** " + info.getCachedJavaRamUsed() + " / " + info.getCachedJavaRamMax() + "\n" +
                "**Machine running since:** " + info.getCachedStartupTime() + "\n" +
                "**Local IP:** " + info.getCachedLocalIP();
    }
}
