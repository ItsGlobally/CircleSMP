package top.itsglobally.circlenetwork.circleSMP.managers;

import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;

public class DataManager extends Manager {

    private static File configDir;

    public DataManager() {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();
        mainConfig = register(new MainConfig(), "config");
        reload();
    }
    public void reload() {
        mainConfig.reload();
    }
    public MainConfig getMainConfig = mainConfig;

    private static MainConfig mainConfig;

    private <T extends BaseConfig> T register(T config, String name) {
        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        return config;
    }

    public class MainConfig extends BaseConfig {
        public int tpaTimeoutSecond = 60;

        public int getTpaTimeoutSecond() {
            return tpaTimeoutSecond;
        }
    }
}
