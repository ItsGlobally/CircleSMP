package top.itsglobally.circlenetwork.circleSMP.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }

    public static void sendMessage(Player player, String message) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player.sendMessage(component);
    }

    public static void sendMessage(Player player1, Player player2, String message) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player1.sendMessage(component);
        player2.sendMessage(component);
    }
    public static void sendMessage(Player player1, Player player2, Component message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }


    public static void sendActionBar(Player player, String message) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player.sendActionBar(component);
    }
    public static void sendDebugActionBar(Player player, String message) {
        if (!player.hasPermission("circlesmp.admin")) return;
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize("&3DEBUG! " + message);
        player.sendActionBar(component);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', title),
                ChatColor.translateAlternateColorCodes('&', subtitle)
        );
    }


    public static String formatMessage(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message).toString();
    }
    public static Component formatMessageToComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}