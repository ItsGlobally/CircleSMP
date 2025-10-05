package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.TpaRequest;
import top.itsglobally.circlenetwork.circleSMP.data.TpaType;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name="tpdeny")
public class tpdeny implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&cUsage: /tpdeny player");
        }
        String targetn = strings[0];
        Player tg = Bukkit.getPlayerExact(targetn);
        if (tg == null) {
            MessageUtil.sendMessage(p, "&7That player is not online!");
            return;
        }
        PlayerManager m = ManagerRegistry.get(PlayerManager.class);
        TpaRequest tr = m.getTpaRequest(tg, p);
        if (tr == null) {
            MessageUtil.sendMessage(p, "&7That player did not send a tpa request to you!");
            return;
        }
        m.removeTpaRequest(p, tr);
        MessageUtil.sendMessage(tg, "&7" + p.getName() + " has denied your request!");
        MessageUtil.sendMessage(p, "&9You have denied " + tg.getName() + "'s request!");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
