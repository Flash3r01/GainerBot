package gainerbot.permissions;

public class WhiteList implements IChannelPermission {
    private final String[] whiteList;
    private final String[] tagWhiteList;

    WhiteList(String[] whiteList, String[] tagWhiteList){
        this.whiteList = whiteList;
        this.tagWhiteList = tagWhiteList;
    }

    @Override
    public boolean isAllowed(String channelName) {
        for(String channel : whiteList){
            if(channelName.equalsIgnoreCase(channel)) return true;
        }
        for(String tag : tagWhiteList){
            if(channelName.toLowerCase().contains(tag)) return true;
        }
        return false;
    }
}
