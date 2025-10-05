package top.itsglobally.circlenetwork.circleSMP;

import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;

public final class CircleSMP extends JavaPlugin {

    public static CircleSMP plugin;

    @Override
    public void onEnable() {
        plugin = this;
        ManagerRegistry.init();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CircleSMP getPlugin() {
        return plugin;
    }
}
