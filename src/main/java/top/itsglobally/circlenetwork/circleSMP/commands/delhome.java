package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "delhome")
public class delhome implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;

        if (args.length < 1) {
            MessageUtil.sendMessage(p, "&7Usage: /delhome <name>");
            return;
        }

        String name = args[0].toLowerCase();

        SMPPlayer sp = ManagerRegistry.get(PlayerManager.class).getPlayer(p);

        if (sp.getPlayerDatas().getHome(name) == null) {
            MessageUtil.sendMessage(p, "&7Home &e" + name + " not found!");
            return;
        }

        sp.getPlayerDatas().removeHome(name);
        sp.updatePlayerDatas();
        MessageUtil.sendMessage(p, "&3Home " + name + " has been removed!");
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return List.of();
        DataManager dm = ManagerRegistry.get(DataManager.class);
        DataManager.PlayerData pd = dm.getPlayerDatas().get(p);
        return pd.getHomes().keySet().stream().toList();
    }
}
