package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.command.CommandSender;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "circlesmp", permission = "circlesmp.admin")
public class circlesmp implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length < 1) return;
        switch (strings[0]) {
            case "reload": {
                ManagerRegistry.get(DataManager.class).reload();
            }
            case "saveall": {
                ManagerRegistry.get(DataManager.class).getPlayerDatas().save();
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
