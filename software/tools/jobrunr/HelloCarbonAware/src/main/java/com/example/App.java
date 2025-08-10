package com.example;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.scheduling.carbonaware.CarbonAware;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.server.carbonaware.CarbonAwareJobProcessingConfiguration;

public class App {
    public static void main(String[] args) throws Exception {
        JobScheduler jobScheduler = JobRunr.configure()
            .useStorageProvider(new InMemoryStorageProvider())
            .useBackgroundJobServer(
                BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration()
                    .andCarbonAwareJobProcessingConfiguration(
                        CarbonAwareJobProcessingConfiguration
                          .usingStandardCarbonAwareJobProcessingConfiguration()
                          .andAreaCode("BE")
                    )
            )
            .useDashboard()
            .initialize()
            .getJobScheduler();

        jobScheduler.enqueue(() -> System.out.println("Hello from JobRunr!"));

        jobScheduler.scheduleRecurrently(
            "carbon-hello",
            CarbonAware.dailyBetween(2, 8),
            () -> System.out.println("Carbon-aware Hello World!")
        );

        System.out.println("Dashboard should be available at http://localhost:8000");
        Thread.currentThread().join();
    }
}
