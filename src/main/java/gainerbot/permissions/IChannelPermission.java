package gainerbot.permissions;

public interface IChannelPermission {

    boolean isAllowed(String channelName);

    class Presets {
        /**Standard Permissions are just listening on the bot-channels.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getBotPermissions(){
            return new WhiteList(new String[]{}, new String[]{"bot"});
        }

        /**Chat Permissions are listening on chat-channels and on the gainerbot-channel.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getChatPermissions(){
            return new WhiteList(new String[]{"ni\uD83C\uDD71\uD83C\uDD71a", "rezeption"}, new String[]{"chat", "bot"});
        }

        /**Elevated Permissions are listening on every Channel, except public and a few exceptions.
         *
         * @return The IChannelPermission-Object
         */
        public static IChannelPermission getElevatedPermissions(){
            return new BlackList(new String[]{"foyer", "klassiker", "zitate"}, new String[]{});
        }
    }
}
