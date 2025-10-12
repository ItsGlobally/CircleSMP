package top.itsglobally.circlenetwork.circleSMP;

import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.managers.ConfigManager;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.command.NontageCommandLoader;
import top.nontage.nontagelib.listener.ListenerRegister;

public final class CircleSMP extends JavaPlugin {

    public static CircleSMP plugin;

    public static CircleSMP getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        ManagerRegistry.init();
        NontageCommandLoader.registerAll(this);
        ListenerRegister.registerAll(this);
    }

    @Override
    public void onDisable() {
        ManagerRegistry.get(ConfigManager.class).getMainConfig().save();
        ManagerRegistry.get(ClaimManager.class).getClaims().flush();
        ManagerRegistry.get(PlayerManager.class).getPlayerDatas().flush();
    }
}
