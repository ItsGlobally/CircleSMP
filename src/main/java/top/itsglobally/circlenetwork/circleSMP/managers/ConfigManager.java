package top.itsglobally.circlenetwork.circleSMP.managers;

import top.itsglobally.circlenetwork.circleSMP.utils.ConfigRegister;
import top.nontage.nontagelib.config.BaseConfig;

public class ConfigManager extends Manager{
    private final MainConfig mainConfig;
    public ConfigManager() {
        mainConfig = ConfigRegister.register(new MainConfig(), "config");
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public static class MainConfig extends BaseConfig {
        public int tpaTimeoutSecond = 60;
        public int maxHomes = 5;

        public int getTpaTimeoutSecond() {
            return tpaTimeoutSecond;
        }

        public int getMaxHomes() {
            return maxHomes;
        }
    }
}
