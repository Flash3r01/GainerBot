package gainerbot.permissions;

public class BlackList implements IChannelPermission {
    private final String[] blackList;
    private final String[] tagBlackList;

    public BlackList(String[] blackList, String[] tagBlackList){
        this.blackList = blackList;
        this.tagBlackList = tagBlackList;
    }

    @Override
    public boolean isAllowed(String channelName) {
        for(String channel : blackList){
            if(channelName.equalsIgnoreCase(channel)) return false;
        }
        for(String tag : tagBlackList){
            if(channelName.toLowerCase().contains(tag)) return false;
        }
        return true;
    }
}
