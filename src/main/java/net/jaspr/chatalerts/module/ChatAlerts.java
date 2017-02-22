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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.jaspr.base.module.Feature;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatAlerts extends Feature {

    private static String DEFAULT_HEADER = "^<[a-zA-Z0-9_]*> ";
    private static String CUSTOM_HEADER = "^\\[\\w] [a-zA-Z0-9_]*: ";
    private boolean alertOnServerMessage;
    private boolean alertOnUserMention;
    private boolean alertOnPrivateMessage; // TODO
    private java.lang.String additionalMatchStrings;
    private java.lang.String starredUsers;

    @Override
    public void setupConfig() {
        alertOnServerMessage = loadPropBool("Alert on all server messages", "Will play an alert sound whenever a server message comes through", true);
        alertOnUserMention = loadPropBool("Alert on username mentions", "Will play an alert sound when your full username is mentioned in chat", true);
        alertOnPrivateMessage = loadPropBool("Alert on private messages", "Will play an alert sound when you receive a private message", true);
        additionalMatchStrings = loadPropString("Additional alert terms", "Comma-separated list of terms which will trigger an alert. Spaces will be ignored.", "");
        starredUsers = loadPropString("Alert on messages from user names", "Comma-separated list of usernames to receive an alert when they chat. Spaces will be ignored.", "");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onChatMessage(ClientChatReceivedEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        String message = event.getMessage().getUnformattedText();
        String username = player.getDisplayNameString();

        if (isProbablyFromCurrentPlayer(message)) {
            player.playSound(SoundEvents.BLOCK_ENDERCHEST_OPEN, 0.5f, 1f);
        }

        String trimmedMessage = getStrippedMessage(message);

        if (alertOnUserMention && trimmedMessage.toLowerCase().contains(username.toLowerCase())) {
            playAlert();
        } else if (alertOnServerMessage && isProbablyFromServer(message)) {
            playAlert();
        } else if (!additionalMatchStrings.isEmpty()) {
            for (String term : additionalMatchStrings.split(",")) {
                if (trimmedMessage.toLowerCase().contains(term.trim().toLowerCase())) {
                    playAlert();
                    return;
                }
            }
        } else if (!starredUsers.isEmpty()) { // TODO REBUILD YOU GOT THIS WRONG
            for (String starredSender : starredUsers.split(",")) {
                if (getSenderName(message).toLowerCase().equals(starredSender.trim().toLowerCase())) {
                    playAlert();
                    return;
                }
            }
        }

    }
    @SideOnly(Side.CLIENT)
    private void playAlert() {
        Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 0.5f, 1f);
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyFromCurrentPlayer(String message) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        String username = player.getDisplayNameString().toLowerCase();
        message = message.toLowerCase();

        return (message.matches("^<" + username + "> .*$") || message.matches("^\\[\\w] " + username + ":(.*)"));
    }

    @SideOnly(Side.CLIENT)
    private Boolean isProbablyFromServer(String message) {
        return (message.startsWith("<Server>") || message.startsWith("[Server]") ||  message.startsWith("<> Server"));
    }

    @SideOnly(Side.CLIENT)
    private String getStrippedMessage(String message) {
        if (message.matches(DEFAULT_HEADER + ".*$")) {
            return message.replaceAll(DEFAULT_HEADER, "");
        }

        if (message.matches(CUSTOM_HEADER + ".*")) {
            return message.replaceAll(CUSTOM_HEADER, "");
        }

        return message;
    }

    @SideOnly(Side.CLIENT)
    private String getSenderName(String message) {
        if (message.matches(DEFAULT_HEADER)) {
            return message.replaceAll("^<([a-zA-Z0-9_]*)>.*$", "$1");
        }
        if (message.matches(CUSTOM_HEADER)) {
            return message.replaceAll("^\\[\\w] ([a-zA-Z0-9_]*):.*$", "$1");
        }
        return "";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

}
