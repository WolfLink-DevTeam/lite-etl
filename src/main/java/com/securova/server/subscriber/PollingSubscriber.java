package com.securova.server.subscriber;

import com.securova.server.Scheduler;
import com.securova.server.data.DataSource;
import com.securova.server.data.SourceData;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PollingSubscriber extends DataSubscriber {

    private ScheduledFuture<?> scheduledFuture = null;
    private volatile DataSource dataSource = null;

    @Override
    void enable(DataSource dataSource) {
        this.dataSource = dataSource;
        scheduledFuture = Scheduler.getInstance()
                .runTaskTimerAsync(this::pollingTask,3,10, TimeUnit.SECONDS);
    }
    void pollingTask() {
        if(dataSource == null) return;
        Set<SourceData> sourceDataSet = dataSource.fetchData();
        if(sourceDataSet.isEmpty()) return;
        sourceDataSet.forEach(this::dataReceived);
    }

    @Override
    void disable() {
        if(scheduledFuture != null) {
            Scheduler.getInstance().cancelTask(scheduledFuture);
            scheduledFuture = null;
            dataSource = null;
        }
    }
}
