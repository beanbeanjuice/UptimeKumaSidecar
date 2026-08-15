package com.beanbeanjuice;

import java.util.MissingFormatArgumentException;
import java.util.Timer;
import java.util.TimerTask;

public class UptimeKumaSidecar {

    private static void startTimer(String pullUrl, String pushUrl, int interval, boolean usePing) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.printf("Pinging %s...\n", pullUrl);

                NetworkUtils.pull(pullUrl).thenAccept((response) -> {
                    System.out.println("Sending result to Uptime Kuma...");
                    NetworkUtils.push(pushUrl, response, usePing);
                });
            }
        };

        timer.schedule(task, 0, interval * 1000L);
    }

    static void main(String[] args) {
        System.out.println("Running setup...");

        String pullUrl;
        String pushUrl;
        int interval;
        boolean usePing;

        // Use Environment Variables
        if (args.length == 0) {
            System.out.println("Using environment variables.");

            pullUrl = EnvironmentUtils.getEnvString("PULL_URL");
            pushUrl = EnvironmentUtils.getEnvString("PUSH_URL");
            interval = EnvironmentUtils.getEnvInt("INTERVAL");
            usePing = EnvironmentUtils.getEnvBoolean("USE_PING");

        } else if (args.length != 4) { // Check length of args
            throw new MissingFormatArgumentException("Missing arguments. Must use PULL_URL, PUSH_URL, INTERGVAL, and USE_PING.");
        } else { // Use command line args
            System.out.println("Using command-line arguments.");
            pullUrl = args[0];
            pushUrl = args[1];
            interval = Integer.parseInt(args[2]);
            usePing = Boolean.parseBoolean(args[3]);
        }

        System.out.println("\nUsing the following settings...");
        System.out.printf("PULL_URL: %s\n", pullUrl);
        System.out.printf("PUSH_URL: %s\n", pushUrl);
        System.out.printf("INTERVAL: %s\n", interval);
        System.out.printf("USE_PING: %s\n\n", usePing);

        System.out.println("Starting uptime checker...");
        startTimer(pullUrl, pushUrl, interval, usePing);
    }
}
