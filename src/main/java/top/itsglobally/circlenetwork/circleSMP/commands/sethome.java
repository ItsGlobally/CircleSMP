package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "sethome")
public class sethome implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&7Usage: /sethome name");
            return;
        }
        DataManager dm = ManagerRegistry.get(DataManager.class);
        DataManager.PlayerData pd = dm.getPlayerDatas().get(p);
        List<Location> allhomes = new ArrayList<>(pd.getHomes().values());
        if (allhomes.size() >= dm.getMainConfig().getMaxHomes()) {
            MessageUtil.sendMessage(p, "&7You've reached the max home amount(" + dm.getMainConfig().getMaxHomes() + "x homes)!");
            return;
        }
        pd.setHome(strings[0], p.getLocation());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
