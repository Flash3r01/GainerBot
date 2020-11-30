package gainerbot.permissions;

public interface IChannelPermission {

    boolean isAllowed(String channelName);

    class Presets {
        /**Standard Permissions are just listening on the bot-channels.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getStandardPermissions(){
            return new WhiteList(new String[]{}, new String[]{"bot"});
        }

        /**Chat Permissions are listening on chat-channels and on the gainerbot-channel.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getChatPermissions(){
            return new WhiteList(new String[]{"gainerbot", "ni\uD83C\uDD71\uD83C\uDD71a"}, new String[]{"chat"});
        }

        /**Elevated Permissions are listening on every Channel, except public and a few exceptions.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getElevatedPermissions(){
            return new BlackList(new String[]{"foyer", "rezeption", "klassiker", "zitate"}, new String[]{});
        }
    }
}
