package top.itsglobally.circlenetwork.circleSMP.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.itsglobally.circlenetwork.circleSMP.data.TpaRequest;
import top.itsglobally.circlenetwork.circleSMP.data.TpaType;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name="tpa")
public class tpa implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&7Usage: /tpa player");
        }
        String targetn = strings[0];
        Player tg = Bukkit.getPlayerExact(targetn);
        if (tg == null) {
            MessageUtil.sendMessage(p, "&7That player is not online!");
            return;
        }
        PlayerManager m = ManagerRegistry.get(PlayerManager.class);
        TpaRequest ctr = m.getTpaRequest(p, tg);
        if (ctr != null) {
            Component c1 = Component.text("You've already sent a tpa request to that player!\n")
                    .color(NamedTextColor.GRAY);
            Component c2 = Component.text("Sent the wrong type of tpa? Click to cancel the previous request!")
                    .color(NamedTextColor.BLUE)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to cancel the previous request!")))
                    .clickEvent(ClickEvent.runCommand("/tpcancel " + tg.getName()));

            MessageUtil.sendMessage(p, c1.append(c2));
            return;
        }
        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                MessageUtil.sendMessage(p, "&7Tpa request to " + tg.getName() + " has expired.");
                MessageUtil.sendMessage(tg, "&7Tpa request from " + p.getName() + " has expired.");
                m.removeTpaRequest(p, m.getTpaRequest(p, tg));
            }
        }.runTaskLater(plugin, 20L * ManagerRegistry.get(DataManager.class).getMainConfig().getTpaTimeoutSecond());
        TpaRequest tr = new TpaRequest(p, tg, bt, TpaType.TPA);

        m.addTpaRequest(p, tr);

        MessageUtil.sendMessage(p, "&9You've sent a tpa request to " + tg.getName() + "! They have " + ManagerRegistry.get(DataManager.class).getMainConfig().getTpaTimeoutSecond() + " seconds to accept!");
        MessageUtil.sendMessage(tg, "&9" + p.getName() + " has sent you a tpa request! You have " + ManagerRegistry.get(DataManager.class).getMainConfig().getTpaTimeoutSecond() + " seconds to accept!");
        Component c1 = Component.text("Click to accept!\n")
                .color(NamedTextColor.BLUE)
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept!").color(NamedTextColor.BLUE)))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + p.getName()));
        Component c2 = Component.text("Click to deny!")
                .color(NamedTextColor.BLUE)
                .hoverEvent(HoverEvent.showText(Component.text("Click to deny!").color(NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + p.getName()));
        MessageUtil.sendMessage(tg, c1.append(c2));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
