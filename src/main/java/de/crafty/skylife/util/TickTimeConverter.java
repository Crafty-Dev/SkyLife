package de.crafty.skylife.util;

public class TickTimeConverter {



    public static long seconds(float seconds){
        return (long) (seconds * 20L);
    }

    public static long minutes(float minutes){
        return (long) (minutes * seconds(60));
    }
}
