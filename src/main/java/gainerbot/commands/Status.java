package gainerbot.commands;

import gainerbot.services.SystemInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Status extends BaseCommand{
    public Status() {
        super(new String[]{"status", "stats"}, "Shows a bunch of information about the machine the bot is running on.");
    }

    @Override
    public void execute(@Nonnull MessageReceivedEvent event, HashMap<String, String> options, String[] arguments) {
        event.getChannel().sendMessage(systemInfoToString(SystemInfo.get())).queue();
    }

    private String systemInfoToString(SystemInfo info){
        return "Gainerbot is running on a: " + SystemInfo.getArchitecture() + " " + SystemInfo.getOsName() + "\n\n" +
                "CPU Load: " + info.getCachedCpuLoad() + "\n" +
                "Memory: " + info.getCachedSystemRamUsed() + "(J:" + info.getCachedJavaRamUsed() + ") / " + info.getCachedSystemRamMax() + "(J:" + info.getCachedJavaRamMax() + ")\n" +
                "Machine running since: " + info.getCachedStartupTime() + "\n" +
                "Local IP: " + info.getCachedLocalIP();
    }
}
