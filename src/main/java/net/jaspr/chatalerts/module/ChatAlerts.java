/**
 * This class was implemented by <JaSpr>. It is distributed as part
 * of the ChatAlerts Mod.
 * https://github.com/JaSpr/FasterLadderClimbing
 *
 * ChatAlerts is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * This class was derived from works created by <Vazkii> which were distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */
package net.jaspr.chatalerts.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatAlerts extends Feature {

    private static String USER_NAME_PATTERN = "([a-zA-Z0-9_]*)";
    private static String DEFAULT_HEADER = "^<" + USER_NAME_PATTERN + "> ";
    private static String CUSTOM_HEADER = "^\\[\\w] " + USER_NAME_PATTERN + ": ";
    private static String PRIVATE_MESSAGE_HEADER = "\\[" + USER_NAME_PATTERN + " \\-> " + USER_NAME_PATTERN + "\\]: ";
    private boolean alertOnServerMessage;
    private boolean alertOnUserMention;
    private boolean alertOnPrivateMessage;
    private boolean useNoisyAlert;
    private java.lang.String[] additionalMatchTerms;
    private java.lang.String[] starredUsers;

    @Override
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

        String message = event.getMessage().getUnformattedText();
        String username = player.getDisplayNameString();
        String strippedMessage = getStrippedMessage(message);
        String senderName = getSenderName(message);

        // Outgoing chat message
        if (isProbablyFromCurrentPlayer(message)) {
            // we don't want alerts for our own messages.
            return;
        }

        // Incoming private message
        if (alertOnPrivateMessage && isProbablyPrivateMessage(message)) {
            FMLLog.info("[ChatAlerts] New private message from `" + getSenderName(message) + "` to `" + getPrivateMessageRecipient(message) + "`.");
            String recipient = getPrivateMessageRecipient(message).toLowerCase();
            if (recipient.equals("me") || recipient.equals(username.toLowerCase())) {
                playAlert(message);
                return;
            }
        }

        // No messages for current player status changes
        if (isCurrentPlayerAFKMessage(message, player) || isCurrentPlayerJoinMessage(message, player) || isCurrentPlayerAchievementMessage(message, player)) {
            return;
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

    @SideOnly(Side.CLIENT)
    private void playAlert(String message) {
        FMLLog.info("[ChatAlerts] Playing alert for message: " + message);

        SoundEvent notification = SoundEvents.BLOCK_GLASS_BREAK;
        float volume = 0.5f;

        if (useNoisyAlert) {
            notification = SoundEvents.BLOCK_ANVIL_PLACE;
            volume = 0.7f;
        }

        Minecraft.getMinecraft().player.playSound(notification, volume, 1f);
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyFromCurrentPlayer(String message) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        String username = player.getDisplayNameString();
        String senderName = getSenderName(message);

        return (senderName.equals(username)) || (isProbablyPrivateMessage(message) && senderName.toLowerCase().equals("me"));
    }

    @SideOnly(Side.CLIENT)
    private boolean isCurrentPlayerAFKMessage(String message, EntityPlayer player) {
        return message.startsWith("*" + player.getDisplayNameString()) && message.contains("AFK");
    }

    @SideOnly(Side.CLIENT)
    private boolean isCurrentPlayerJoinMessage(String message, EntityPlayer player) {
        return message.equals(player.getDisplayNameString() + " joined the game");
    }

    @SideOnly(Side.CLIENT)
    private boolean isCurrentPlayerAchievementMessage(String message, EntityPlayer player) {
        return message.startsWith(player.getDisplayNameString() + " has just earned the achievement");
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyFromServer(String message) {
        return (message.startsWith("<Server>") || message.startsWith("[Server]") ||  message.startsWith("<> Server"));
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyPrivateMessage(String message) {
        return message.matches(PRIVATE_MESSAGE_HEADER + ".*$");
    }

    @SideOnly(Side.CLIENT)
    private String getPrivateMessageRecipient(String message) {
        if (message.matches(PRIVATE_MESSAGE_HEADER + ".*$")) {
            return message.replaceAll(PRIVATE_MESSAGE_HEADER + ".*$", "$2").trim();
        }
        return "";
    }

    @SideOnly(Side.CLIENT)
    private String getStrippedMessage(String message) {
        if (message.matches(DEFAULT_HEADER + ".*$")) {
            return message.replaceAll(DEFAULT_HEADER, "").trim();
        }
        if (message.matches(CUSTOM_HEADER + ".*$")) {
            return message.replaceAll(CUSTOM_HEADER, "").trim();
        }
        if (message.matches(PRIVATE_MESSAGE_HEADER + ".*$")) {
            return message.replaceAll(PRIVATE_MESSAGE_HEADER, "").trim();
        }

        return message;
    }

    @SideOnly(Side.CLIENT)
    private String getSenderName(String message) {
        if (message.matches(DEFAULT_HEADER + ".*$")) {
            return message.replaceAll(DEFAULT_HEADER + ".*$", "$1").trim();
        }
        if (message.matches(PRIVATE_MESSAGE_HEADER + ".*$")) {
            return message.replaceAll(PRIVATE_MESSAGE_HEADER + ".*$", "$1").trim();
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
