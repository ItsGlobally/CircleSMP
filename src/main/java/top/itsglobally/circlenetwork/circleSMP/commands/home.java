package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "home")
public class home implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;

        if (args.length < 1) {
            MessageUtil.sendMessage(p, "&7");
            return;
        }

        String name = args[0].toLowerCase();

        DataManager dm = ManagerRegistry.get(DataManager.class);
        DataManager.PlayerData pd = dm.getPlayerDatas().get(p);

        Location loc = pd.getHome(name);
        if (loc == null) {
            MessageUtil.sendMessage(p, "&7home &e" + name + " does not exist!");
            return;
        }

        p.teleport(loc);
        MessageUtil.sendMessage(p, "&9Teleported to home " + name + "!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
