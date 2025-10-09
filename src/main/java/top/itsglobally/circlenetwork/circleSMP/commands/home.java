package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.Location;
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

@CommandInfo(name = "home")
public class home implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        SMPPlayer sp = ManagerRegistry.get(PlayerManager.class).getPlayer(p);

        if (args.length < 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("&3---------------------------------\n");
            sb.append("Your homes: \n");
            for (String name : sp.getPlayerDatas().listHomes()) {
                sb.append("- ").append(name).append("\n");
            }
            sb.append("&3---------------------------------");
            MessageUtil.sendMessage(p, sb.toString());
            return;
        }

        String name = args[0].toLowerCase();

        Location loc = sp.getPlayerDatas().getHome(name);
        if (loc == null) {
            MessageUtil.sendMessage(p, "&7home " + name + " does not exist!");
            return;
        }

        p.teleport(loc);
        MessageUtil.sendMessage(p, "&3Teleported to home " + name + "!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return List.of();
        DataManager dm = ManagerRegistry.get(DataManager.class);
        DataManager.PlayerData pd = dm.getPlayerDatas().get(p);
        return pd.getHomes().keySet().stream().toList();
    }
}
