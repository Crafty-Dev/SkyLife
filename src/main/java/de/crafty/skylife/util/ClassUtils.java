package de.crafty.skylife.util;

import de.crafty.skylife.SkyLife;

import java.lang.reflect.InvocationTargetException;

public class ClassUtils {


    public static <T> T createInstance(Class<T> clazz){
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            SkyLife.LOGGER.error("Failed to create instance of class: {}", clazz.getName());
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T createInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args){
        try {
            return clazz.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            SkyLife.LOGGER.error("Failed to create instance of class: {}", clazz.getName());
        }
        return null;
    }

}
