package com.securova.server;

import java.util.concurrent.*;

public class Scheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static volatile Scheduler instance = null;

    public static Scheduler getInstance() {
        if (instance == null) {
            synchronized (Scheduler.class) {
                if (instance == null) {
                    instance = new Scheduler();
                }
            }
        }
        return instance;
    }

    private Scheduler() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownNow));
    }
    public void shutdownNow() {
        scheduler.shutdownNow();
    }

    public ScheduledFuture<?> runTaskTimerAsync(
            Runnable runnable,
            long initialDelay,
            long period,
            TimeUnit unit
    ) {
        return scheduler.scheduleAtFixedRate(()-> CompletableFuture.runAsync(runnable).exceptionallyAsync(throwable -> {
            throwable.printStackTrace();
            return null;
        }), initialDelay, period, unit);
    }

    public void cancelTask(ScheduledFuture<?> future) {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }
}
