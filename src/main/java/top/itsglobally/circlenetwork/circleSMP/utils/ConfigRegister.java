package top.itsglobally.circlenetwork.circleSMP.utils;

import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;

import static top.itsglobally.circlenetwork.circleSMP.CircleSMP.plugin;

public class ConfigRegister {
    public static <T extends BaseConfig> T register(T config, String name) {
        File configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();
        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        return config;
    }
}
