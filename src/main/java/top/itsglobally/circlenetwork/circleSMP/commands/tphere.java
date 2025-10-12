package top.itsglobally.circlenetwork.circleSMP.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.itsglobally.circlenetwork.circleSMP.data.TpaRequest;
import top.itsglobally.circlenetwork.circleSMP.data.TpaType;
import top.itsglobally.circlenetwork.circleSMP.managers.ConfigManager;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "tphere")
public class tphere implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&7Usage: /tphere player");
            return;
        }
        String targetn = strings[0];
        Player tg = Bukkit.getPlayerExact(targetn);
        if (tg == null) {
            MessageUtil.sendMessage(p, "&7That player is not online!");
            return;
        }
        PlayerManager m = ManagerRegistry.get(PlayerManager.class);
        SMPPlayer sp = m.getPlayer(p);
        if (sp == null) {
            MessageUtil.sendMessage(p, "???");
            return;
        }
        TpaRequest ctr = sp.getTpaRequest(tg);
        if (ctr != null) {
            Component c1 = Component.text("You've already sent a tphere request to that player!\n")
                    .color(NamedTextColor.GRAY);
            Component c2 = Component.text("Sent the wrong type of tpa? Click to cancel the previous request!")
                    .color(NamedTextColor.DARK_AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to cancel the previous request!")))
                    .clickEvent(ClickEvent.runCommand("/tpcancel " + tg.getName()));

            MessageUtil.sendMessage(p, c1.append(c2));
            return;
        }
        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                MessageUtil.sendMessage(p, "&7Tphere request to " + tg.getName() + " has expired.");
                MessageUtil.sendMessage(tg, "&7Tphere request from " + p.getName() + " has expired.");
                sp.removeTpaRequest(sp.getTpaRequest(tg));
            }
        }.runTaskLater(plugin, 20L * ManagerRegistry.get(ConfigManager.class).getMainConfig().getTpaTimeoutSecond());
        TpaRequest tr = new TpaRequest(p, tg, bt, TpaType.TPHERE);

        sp.addTpaRequest(tr);

        MessageUtil.sendMessage(p, "&3You've sent a tphere request to " + tg.getName() + "! They have" + ManagerRegistry.get(ConfigManager.class).getMainConfig().getTpaTimeoutSecond() + " seconds to accept!");
        MessageUtil.sendMessage(tg, "&3" + p.getName() + " has sent you a tphere request! You have" + ManagerRegistry.get(ConfigManager.class).getMainConfig().getTpaTimeoutSecond() + " seconds to accept!");
        Component c1 = Component.text("Click to accept!\n")
                .color(NamedTextColor.DARK_AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept!").color(NamedTextColor.DARK_AQUA)))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + tg.getName()));
        Component c2 = Component.text("Click to deny!")
                .color(NamedTextColor.DARK_AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("Click to deny!").color(NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + tg.getName()));
        MessageUtil.sendMessage(tg, c1.append(c2));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
