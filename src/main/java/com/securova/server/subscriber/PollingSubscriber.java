package com.securova.server.subscriber;

import com.securova.server.Scheduler;
import com.securova.server.data.DataSource;
import com.securova.server.data.SourceData;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PollingSubscriber extends DataSubscriber {

    private ScheduledFuture<?> scheduledFuture = null;

    @Override
    void enable() {
        scheduledFuture = Scheduler.getInstance()
                .runTaskTimerAsync(this::pollingTask,3,10, TimeUnit.SECONDS);
    }
    void pollingTask() {
        if(!enable) return;
        DataSource dataSource = pipeline.getSource();
        if(dataSource == null) return;
        Collection<SourceData> sourceDataSet = dataSource.fetchData();
        if(sourceDataSet.isEmpty()) return;
        sourceDataSet.forEach(this::dataReceived);
    }

    @Override
    void disable() {
        if(scheduledFuture != null) {
            Scheduler.getInstance().cancelTask(scheduledFuture);
            scheduledFuture = null;
        }
    }
}
