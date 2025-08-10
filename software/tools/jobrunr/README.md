# JobRunr v8

An extremely easy way to perform background processing in Java. Backed by persistent storage. Open and free for commercial use.

## Carbon aware jobs

This release introduces a slew of new features such as Carbon Aware Job Processing; a new feature that optimizes job execution based on grid carbon intensity, helping reduce your application’s environmental impact by running jobs when cleaner energy is available.

## Example from the website

For instance, suppose you have a daily recurring job that generates invoice PDF files and sends them out to customers. That job can be configured by using a simple cron, `e.g. 0 5 * * *`. Each early morning at 5AM, it will start to process data. But what if two hours later would be a better moment to reduce the CO2 footprint of the application?

Instead of scheduling jobs like this:

```java
BackgroundJob.scheduleRecurrently("0 5 * * *", () -> pdfService.generate());
```

You can now do this:

```java
BackgroundJob.scheduleRecurrently("0 5 * * * [PT1H/PT3H]", 
    () -> pdfService.generate());
// or
BackgroundJob.scheduleRecurrently(CarbonAware.dailyBetween(4, 8), 
    () -> pdfService.generate());
// or
BackgroundJob.scheduleRecurrently(CarbonAware.cron("0 5 * * *", Duration.of(1, HOURS), Duration.of(3, HOURS)),
    () -> pdfService.generate());
```

The configured margin [from, to] adds slack to the schedule time of the job. Depending on the forecast put out by the data provider, the job can be enqueued an hour sooner ([PT1H) or at most three hours later (/PT3H]). JobRunr comes equipped with multiple expressive APIs that help ease the configuration of these Carbon Aware Jobs, by using the CarbonAware class or the CarbonAwarePeriod class for fire-and-forget jobs.

As soon as recurring jobs with a Carbon Aware margin are scheduled, a job is created ahead of time in a “Pending” state, waiting to be scheduled depending on the Carbon Intensity forecast. Once JobRunr figures out when exactly to run the job, it will set a scheduled time to move on to the next states: “Scheduled”, and when it is time to execute, “Enqueued” and “Processing”.

There are also reference links on the website for more information on how to use Carbon Aware Jobs.

- The [How To Reduce Your Carbon Impact With Carbon Aware Jobs](https://www.jobrunr.io/en/guides/intro/how-to-reduce-carbon-impact-with-carbon-aware-jobs/) guide for more examples and how to correctly configure your setup.
- The [Carbon Aware Processing: Configuration](https://www.jobrunr.io/en/documentation/configuration/carbon-aware/) documentation and
- The [Background Methods: Carbon Aware](https://www.jobrunr.io/en/documentation/background-methods/carbon-aware-jobs/) Jobs documentation for more in-depth information about the feature.

## An Example of a Carbon Aware Job

Add this block in `pom.xml` to use JobRunr v8:

```xml
    <!-- JobRunr core (v8) -->
    <dependency>
      <groupId>org.jobrunr</groupId>
      <artifactId>jobrunr</artifactId>
      <version>${jobrunr.version}</version>
    </dependency>

        <!-- Jackson (JobRunr needs a Json provider) -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.15.2</version>
    </dependency>
```

And, then we can configure the jobrunr.

```java
package com.example;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.carbonaware.CarbonAware;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.server.carbonaware.CarbonAwareJobProcessingConfiguration;

public class App {
    public static void main(String[] args) throws Exception {
        // Configure JobRunr with in-memory storage (good for testing/demo)
        JobScheduler jobScheduler = JobRunr.configure()
            .useStorageProvider(new InMemoryStorageProvider())
            .useBackgroundJobServer(
                BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration()
                    // enable & configure Carbon Aware processing (area code = BE for Belgium)
                    .andCarbonAwareJobProcessingConfiguration(
                        CarbonAwareJobProcessingConfiguration
                          .usingStandardCarbonAwareJobProcessingConfiguration()
                          .andAreaCode("BE") // set to your datacenter / region (ISO 3166-2)
                    )
            )
            .useDashboard() // launches dashboard (default port 8000)
            .initialize()
            .getJobScheduler();

        // 1) Simple fire-and-forget hello
        jobScheduler.enqueue(() -> System.out.println("Hello from JobRunr!"));

        // 2) Carbon-aware recurring job (daily, but allow a margin)
        // Use the expressive CarbonAware API: dailyBetween(2, 8) => schedule somewhere between 02:00 and 08:00 local time,
        // JobRunr will select the best (lowest-carbon) moment in that interval.
        jobScheduler.scheduleRecurrently(
            "carbon-hello",                       // job id
            CarbonAware.dailyBetween(2, 8),      // preferred daily time + margin window
            () -> System.out.println("Carbon-aware Hello World!"));

        System.out.println("Dashboard should be available at http://localhost:8000");
        // keep JVM alive so background server & dashboard keep running
        Thread.currentThread().join();
    }
}
```

Complete example is attached in [`HelloCarbonAware`](./HelloCarbonAware/) directory.

### How to run the example

Run the below bash command to run the example:

```bash
$ mvn -DskipTests compile exec:java -Dexec.mainClass="com.example.App"
Dashboard should be available at http://localhost:8000
Hello from JobRunr!
```

## Credits

1. [JobRunr v8 Release Notes](https://www.jobrunr.io/en/)
