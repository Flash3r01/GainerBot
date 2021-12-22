package gainerbot.slashCommand.commands.audio.audioControllerMessage;

import gainerbot.GainerBot;
import gainerbot.audio.AudioHelper;
import gainerbot.audio.AudioManager;
import gainerbot.audio.TrackScheduler;
import gainerbot.audio.TrackSchedulerLoadHandler;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

//TODO Buttons/Interactive Messages should also get a System similar to SlashCommands.
public class AudioControllerMessage {
    private final String guildId;
    private final String channelId;
    private final TrackScheduler trackScheduler;
    private final AudioControllerMessageUpdater updater;
    private String threadId;
    private String messageId;

    public AudioControllerMessage(String guildId, String channelId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.trackScheduler = TrackScheduler.fromPlayerManager(AudioManager.getAudioManager().getPlayerManager());
        this.updater = new AudioControllerMessageUpdater(this);
    }


    public void play(String identifier, AudioChannel audioChannel, Consumer<String> messageConsumer){
        trackScheduler.getAudioPlayer().addListener(updater);
        TrackSchedulerLoadHandler.loadAudio(identifier, trackScheduler, (msg, success) -> {
            if (success){
                sendAudioControllerMessage(true);
                //noinspection ConstantConditions
                messageConsumer.accept(msg + "\n\nAudio controls are available in: "+GainerBot.jdaInstance.getGuildById(guildId).getThreadChannelById(threadId).getAsMention());
                AudioHelper.connectToChannel(audioChannel, trackScheduler.getAudioPlayer());
                TextChannel textChannel = GainerBot.jdaInstance.getTextChannelById(channelId);
                if (textChannel == null){
                    System.out.println("Can not send the Queued-Message for archiving, because TextChannel was not found.");
                }else{
                    textChannel.sendMessage(identifier+"\n"+msg).queue(archiveMsg -> archiveMsg.suppressEmbeds(true).queue());
                }
            }else{
                messageConsumer.accept(msg);
            }
        });
    }

    public void pause(){
        if (trackScheduler.getAudioPlayer().isPaused()) return;

        trackScheduler.getAudioPlayer().setPaused(true);
    }

    public void resume(){
        if (!trackScheduler.getAudioPlayer().isPaused()) return;

        trackScheduler.getAudioPlayer().setPaused(false);
    }

    public void skip(){
        trackScheduler.skip();
    }

    public void stop(){
        trackScheduler.stop();
        deleteAudioControllerMessage();
        Guild guild = GainerBot.jdaInstance.getGuildById(guildId);
        if (guild == null) return;
        guild.getAudioManager().setSendingHandler(null);
        if (!AudioControllerMessageManager.removeAudioControllerMessage(guildId)) System.out.println("INFO: Was not able to remove AudioControllerMessage for guild: "+guildId);
    }

    public void redraw(){
        sendAudioControllerMessage(false);
    }

    public void deleteAudioControllerMessage() {
        if (messageId == null && threadId == null) return;
        trackScheduler.getAudioPlayer().removeListener(updater);
        Guild guild = GainerBot.jdaInstance.getGuildById(guildId);
        if (guild == null){
            System.out.println("WARNING: AudioControllerMessage has a invalid guildId. Cant delete the audio control message. guildId: "+guildId+".");
            return;
        }
        ThreadChannel threadChannel = guild.getThreadChannelById(threadId);
        if (threadChannel != null){
            threadChannel.delete().queue();
        }
        messageId = null;
        threadId = null;
    }

    private void sendAudioControllerMessage(boolean createIfNeeded){
        if (!createIfNeeded && messageId == null) return;

        Guild guild = GainerBot.jdaInstance.getGuildById(guildId);
        if (guild == null){
            System.out.println("WARNING: AudioControllerMessage has a invalid guildId. Audio control message is not being sent. guildId: "+guildId+".");
            return;
        }

        if (messageId == null || threadId == null){
            if (!createIfNeeded) return;

            ListenerAdapter threadCreationMessageDeleter = new ListenerAdapter() {
                @Override
                public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
                    if (!event.isFromGuild()) return;
                    if (!event.getChannel().getId().equals(channelId)) return;
                    if (!(event.getMessage().getType() == MessageType.THREAD_CREATED)) return;
                    event.getMessage().delete().queue();
                    GainerBot.jdaInstance.removeEventListener(this);
                }
            };
            GainerBot.jdaInstance.addEventListener(threadCreationMessageDeleter);
            GainerBot.executorService.submit(() ->{
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                } finally {
                    GainerBot.jdaInstance.removeEventListener(threadCreationMessageDeleter);
                }
            });

            TextChannel textChannel = guild.getTextChannelById(channelId);
            if (textChannel == null){
                System.out.println("WARNING: AudioControllerMessage has a invalid channelId. Audio control message is not being sent. guildId: "+guildId+" channelId: "+channelId+".");
                return;
            }
            ThreadChannel threadChannel = textChannel.createThreadChannel("Audio Player").complete();

            this.threadId = threadChannel.getId();
            //noinspection ConstantConditions
            guild.findMembers(member -> member.getVoiceState().inAudioChannel() && !member.getUser().isBot())
                    .onSuccess(voiceMembers -> voiceMembers.forEach(member -> threadChannel.addThreadMember(member).queue()))
                    .onSuccess((members) -> {
                        //TODO The retrieval of the messageId might be after someone clicked on a button. (race condition)
                        threadChannel.sendMessage(createMessage()).queueAfter(1, TimeUnit.SECONDS, msg -> messageId = msg.getId());
            });
        }else{
            ThreadChannel threadChannel = guild.getThreadChannelById(threadId);
            if (threadChannel == null) {
                System.out.println("No thread with id: "+threadId+" in guild: "+guildId+" found. Not updating AudioControllerMessage.");
                return;
            }
            threadChannel.editMessageById(messageId, createMessage()).queue();
        }
    }

    public void handleButtonClicked(ButtonClickEvent event){
        if (event.getGuild() == null) return;
        if (!event.getGuild().getId().equals(guildId)) return;

        switch (event.getComponentId()){
            case "pause":
                event.deferEdit().queue();
                pause();
                break;
            case "resume":
                event.deferEdit().queue();
                resume();
                break;
            case "skip":
                event.deferEdit().queue();
                skip();
                break;
            case "stop":
                event.deferEdit().queue();
                stop();
                break;
            default:
        }
    }

    private Message createMessage(){
        MessageBuilder builder = new MessageBuilder();
        if (!trackScheduler.getAudioPlayer().isPaused()){
            builder.append("🎵 Currently Playing 🎵\n");
        }else{
            builder.append("⏸ Paused ⏸\n");
        }
        builder.append(AudioHelper.toTrackInfoString(trackScheduler.getAudioPlayer().getPlayingTrack()))
                .append("\n\nQueued:\n");

        if (trackScheduler.getQueue().isEmpty()){
            builder.append("None\n");
        }else{
            trackScheduler.getQueue().parallelStream().forEachOrdered((track) -> builder.append(AudioHelper.toTrackInfoString(track)).append("\n"));
        }

        builder.setActionRows(ActionRow.of(
                Button.primary("pause", "Pause").withEmoji(Emoji.fromMarkdown("⏸"))
                        .withDisabled(trackScheduler.getAudioPlayer().isPaused()),
                Button.primary("resume", "Resume").withEmoji(Emoji.fromMarkdown("▶"))
                        .withDisabled(!trackScheduler.getAudioPlayer().isPaused()),
                Button.primary("skip", "Skip").withEmoji(Emoji.fromMarkdown("⏩")),
                Button.danger("stop", "Stop").withEmoji(Emoji.fromMarkdown("⏹"))
        ));

        return builder.build();
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }
}
