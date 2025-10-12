package top.itsglobally.circlenetwork.circleSMP.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.data.Tmp;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;
import java.util.UUID;

@CommandInfo(name="claim")
public class claim implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 1) return;
        ClaimManager cm = ManagerRegistry.get(ClaimManager.class);
        switch (strings[0]) {
            case "create": {
                String arg = strings[1];
                boolean hasSameName = cm.getClaims(p.getUniqueId()).stream()
                        .anyMatch(c -> c.getName().equalsIgnoreCase(arg));

                if (hasSameName) {
                    MessageUtil.sendMessage(p, "&7You already have a claim with that name!");
                    return;
                }
                if (Tmp.cl1.get(p.getUniqueId()) == null || Tmp.cl2.get(p.getUniqueId()) == null) {
                    MessageUtil.sendMessage(p, "&7Set pos 1 and pos 2 first!");
                    return;
                }
                Claim newClaim = new Claim(arg, p.getUniqueId(), UUID.randomUUID());
                newClaim.addRegion(Tmp.cl1.get(p.getUniqueId()), Tmp.cl2.get(p.getUniqueId()));
                cm.registerClaim(newClaim);
                break;
            }
            case "pos1": {
                Tmp.cl1.put(p.getUniqueId(), p.getLocation());
                MessageUtil.sendMessage(p, "&3Set pos 1!");
                break;
            }
            case "pos2": {
                Tmp.cl2.put(p.getUniqueId(), p.getLocation());
                MessageUtil.sendMessage(p, "&3Set pos 2!");
                break;
            }
            case "list": {
                StringBuilder sb = new StringBuilder();
                sb.append("&3---------------------------------\n");
                sb.append("&rYour claim:\n");
                for (Claim c : cm.getClaims(p.getUniqueId())) {
                    sb.append("&r").append(c.getName()).append("\n");
                }
                sb.append("\n");
                sb.append("&3---------------------------------");
                MessageUtil.sendMessage(p, sb.toString());
                break;
            }
            case "remove": {
                String arg = strings[1];
                Claim c = cm.getClaims(p.getUniqueId()).stream()
                        .filter(claim -> claim.getName().equals(arg))
                        .findFirst()
                        .orElse(null);
                if (c == null) {
                    MessageUtil.sendMessage(p, "&7That claim does not exist!");
                    return;
                }
                cm.unregisterClaim(c);
                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
