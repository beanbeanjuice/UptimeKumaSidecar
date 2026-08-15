package com.beanbeanjuice;

public class EnvironmentUtils {

    private static String getEnv(String name) {
        String value = System.getenv(name);

        if (value == null) throw new NullPointerException("Environment variable " + name + " is missing.");

        return value;
    }

    public static String getEnvString(String name) {
        return getEnv(name);
    }

    public static int getEnvInt(String name) {
        return Integer.parseInt(getEnv(name));
    }

    public static boolean getEnvBoolean(String name) {
        return Boolean.parseBoolean(getEnv(name));
    }

}
