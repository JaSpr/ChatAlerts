/**
 * This class was implemented by <JaSpr>. It is distributed as part
 * of the ChatAlerts Mod.
 * https://github.com/JaSpr/ChatAlerts
 *
 * ChatAlerts is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */
package net.jaspr.chatalerts.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.jaspr.chatalerts.ChatAlerts;

public class AlertHandler extends Feature {

    private static String USER_NAME_PATTERN = "([a-zA-Z0-9_]*)";

    private static String VANILLA_HEADER = "^<" + USER_NAME_PATTERN + "> ";
    private static String VANILLA_PM_HEADER = USER_NAME_PATTERN + " whispers to you: ";

    private static String CUSTOM_HEADER = "^\\[\\w] " + USER_NAME_PATTERN + ": ";
    private static String CUSTOM_PM_HEADER = "\\[" + USER_NAME_PATTERN + " \\-> " + USER_NAME_PATTERN + "\\]: ";

    private boolean alertOnServerMessage;
    private boolean alertOnUserMention;
    private boolean alertOnPrivateMessage;
    private boolean useNoisyAlert;
    private java.lang.String[] additionalMatchTerms;
    private java.lang.String[] starredUsers;

    @Override
    @SideOnly(Side.CLIENT)
    public void setupConfig() {
        useNoisyAlert = loadPropBool("useNoisyAlert", "Plays a more piercing alert sound (the anvil) to REALLY get your attention.", false);
        alertOnServerMessage = loadPropBool("serverMsgAlerts", "Play alert whenever a server message comes through", true);
        alertOnUserMention = loadPropBool("mentionAlerts", "Play alert when current player is mentioned in chat", true);
        alertOnPrivateMessage = loadPropBool("pmAlerts", "Play alert when current player receive a private message", true);
        additionalMatchTerms = loadPropStringList("customAlertTerms", "List of words or phrases to listen for. (Plays alert when they are mentioned in chat).  + to add a new word or phrase, X to remove.", new String[]{});
        starredUsers = loadPropStringList("customPlayerAlerts", "List of users (full Minecraft usernames) to listen for (Plays alert when they post in chat). + to add a new user, X to remove.", new String[]{});
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onChatMessage(ClientChatReceivedEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        String message = stripColors(event.getMessage().getUnformattedText());
        String username = player.getDisplayNameString();

        String strippedMessage = getStrippedMessage(message);
        String senderName = getSenderName(message);


        // Outgoing chat message
        if (isProbablyFromCurrentPlayer(message) || message.startsWith(username)) {
            // we don't want alerts for our own messages.
            return;
        }

        // No messages for current player status changes
        if (isCurrentPlayerAFKMessage(message, player) || isCurrentPlayerJoinMessage(message, player) || isCurrentPlayerAchievementMessage(message, player)) {
            return;
        }

        // Incoming private message
        if (alertOnPrivateMessage && isProbablyPrivateMessage(message)) {
            String recipient = getPrivateMessageRecipient(message).toLowerCase();
            if (recipient.equals("me") || recipient.equals(username.toLowerCase())) {
                playAlert(message);
                return;
            }
        }

        // Username was mentioned in an incoming message
        if (alertOnUserMention && strippedMessage.toLowerCase().contains(username.toLowerCase())) {
            playAlert(message);
            return;
        }

        // Incoming server message.
        if (alertOnServerMessage && isProbablyFromServer(message)) {
            playAlert(message);
            return;
        }

        // Matches user-defined chat terms or senders.
        if (additionalMatchTerms.length > 0 || starredUsers.length > 0) {
            for (String term : additionalMatchTerms) {
                if (!term.trim().isEmpty() && strippedMessage.toLowerCase().contains(term.trim().toLowerCase())) {
                    playAlert(message);
                    return;
                }
            }
            for (String starredSender : starredUsers) {
                if (!senderName.isEmpty() && senderName.toLowerCase().equals(starredSender.trim().toLowerCase())) {
                    playAlert(message);
                    return;
                }
            }
        }
    }

    private String stripColors(String message) {
        return message.replaceAll("\u00a7[0-9a-fA-F]", "");
    }

    @SideOnly(Side.CLIENT)
    private void playAlert(String message) {
        SoundEvent notification = SoundEvents.BLOCK_GLASS_BREAK;
        float volume = 0.5f;

        if (useNoisyAlert) {
            notification = SoundEvents.BLOCK_ANVIL_PLACE;
            volume = 0.7f;
        }

        ChatAlerts.logger.info("Playing alert for message: \"" + message + "\"");

        Minecraft.getMinecraft().player.playSound(notification, volume, 1f);
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyFromCurrentPlayer(String message) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        String username = player.getDisplayNameString();
        String senderName = getSenderName(message);

        return (senderName.equals(username)) || (isProbablyPrivateMessage(message) && senderName.toLowerCase().equals("me"));
    }

    private boolean isCurrentPlayerAFKMessage(String message, EntityPlayer player) {
        return message.startsWith("*" + player.getDisplayNameString()) && message.contains("AFK");
    }

    private boolean isCurrentPlayerJoinMessage(String message, EntityPlayer player) {
        return message.equals(player.getDisplayNameString() + " joined the game");
    }

    private boolean isCurrentPlayerAchievementMessage(String message, EntityPlayer player) {
        return message.startsWith(player.getDisplayNameString() + " has just earned the achievement");
    }

    private Boolean isProbablyFromServer(String message) {
        return (message.startsWith("<Server>") || message.startsWith("[Server]") ||  message.startsWith("<> Server"));
    }

    private Boolean isProbablyPrivateMessage(String message) {
        return message.matches(CUSTOM_PM_HEADER + ".*$") || isVanillaPrivateMessage(message);
    }

    private Boolean isVanillaPrivateMessage(String message) {
        return message.matches(VANILLA_PM_HEADER + ".*$");
    }

    private String getPrivateMessageRecipient(String message) {
        if (isVanillaPrivateMessage(message)) {
            return "me";
        }
        if (message.matches(CUSTOM_PM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_PM_HEADER + ".*$", "$2").trim();
        }
        return "";
    }

    private String getStrippedMessage(String message) {
        if (message.matches(VANILLA_HEADER + ".*$")) {
            return message.replaceAll(VANILLA_HEADER, "").trim();
        }
        if (message.matches(CUSTOM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_HEADER, "").trim();
        }
        if (message.matches(VANILLA_PM_HEADER + ".*$")) {
            return message.replaceAll(VANILLA_PM_HEADER, "").trim();
        }
        if (message.matches(CUSTOM_PM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_PM_HEADER, "").trim();
        }

        return message;
    }

    private String getSenderName(String message) {
        if (message.matches(VANILLA_HEADER + ".*$")) {
            return message.replaceAll(VANILLA_HEADER + ".*$", "$1").trim();
        }
        if (message.matches(CUSTOM_PM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_PM_HEADER + ".*$", "$1").trim();
        }
        if (message.matches(VANILLA_PM_HEADER + ".*$")) {
            return message.replaceAll(VANILLA_PM_HEADER + ".*$", "$1").trim();
        }
        if (message.matches(CUSTOM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_HEADER + ".*$", "$1").trim();
        }
        return "";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

}
