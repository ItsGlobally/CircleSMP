package top.itsglobally.circlenetwork.circleSMP;

import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.command.NontageCommandLoader;

public final class CircleSMP extends JavaPlugin {

    public static CircleSMP plugin;

    @Override
    public void onEnable() {
        plugin = this;
        ManagerRegistry.init();
        NontageCommandLoader.registerAll(this);
    }

    @Override
    public void onDisable() {
    }

    public static CircleSMP getPlugin() {
        return plugin;
    }
}
