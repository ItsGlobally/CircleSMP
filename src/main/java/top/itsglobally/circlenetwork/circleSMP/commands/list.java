package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.command.CommandSender;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "list", override = true)
public class list implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        PlayerManager m = ManagerRegistry.get(PlayerManager.class);
        StringBuilder sb = new StringBuilder();
        sb.append("&3---------------------------------\n");
        sb.append("There are ").append(m.getPlayers().size()).append(" players\n");
        for (SMPPlayer sp : m.getPlayers().values()) {
            sb.append("&3- ").append(sp.getName()).append("\n");
        }
        sb.append("&3---------------------------------");
        commandSender.sendMessage(sb.toString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
