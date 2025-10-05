package top.itsglobally.circlenetwork.circleSMP.utils;

import org.reflections.Reflections;
import java.lang.reflect.Constructor;
import java.util.*;

public final class ManagerRegistry {

    private static final Map<Class<? extends Manager>, Manager> MANAGERS = new HashMap<>();

    public static void init() {
        Reflections reflections = new Reflections("top.itsglobally.circlenetwork.circleSMP.managers");

        Set<Class<? extends Manager>> classes = reflections.getSubTypesOf(Manager.class);

        for (Class<? extends Manager> clazz : classes) {
            try {
                Constructor<? extends Manager> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Manager instance = constructor.newInstance();
                MANAGERS.put(clazz, instance);
                System.out.println("[CircleSMP] Loaded Manager: " + clazz.getSimpleName());
            } catch (Exception e) {
                System.err.println("[CircleSMP] Failed to load Manager: " + clazz.getName());
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

    private ManagerRegistry() {}
}

