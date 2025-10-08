package top.itsglobally.circlenetwork.circleSMP.utils;

import org.bukkit.Bukkit;
import org.reflections.Reflections;
import top.itsglobally.circlenetwork.circleSMP.managers.Manager;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ManagerRegistry {

    private static final Map<Class<? extends Manager>, Manager> MANAGERS = new HashMap<>();

    private ManagerRegistry() {
    }

    public static void init() {
        Reflections reflections = new Reflections("top.itsglobally.circlenetwork.circleSMP.managers");

        Set<Class<? extends Manager>> classes = reflections.getSubTypesOf(Manager.class);

        for (Class<? extends Manager> clazz : classes) {
            try {
                Constructor<? extends Manager> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Manager instance = constructor.newInstance();
                MANAGERS.put(clazz, instance);
                Bukkit.getLogger().info("[CircleSMP] Loaded Manager: " + clazz.getSimpleName());
            } catch (Exception e) {
                Bukkit.getLogger().warning("[CircleSMP] Failed to load Manager: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Manager> T get(Class<T> clazz) {
        return (T) MANAGERS.get(clazz);
    }

    public static Collection<Manager> getAll() {
        return MANAGERS.values();
    }
}

