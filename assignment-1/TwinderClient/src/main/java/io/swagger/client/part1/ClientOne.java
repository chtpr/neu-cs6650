package io.swagger.client.part1;

import static io.swagger.client.constants.EnvironmentConstants.NUM_THREADS;

import io.swagger.client.utilities.StatsGenerator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Client for part one
 */
public class ClientOne {

  public static void main(String[] args) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(NUM_THREADS);
    AtomicInteger successCount = new AtomicInteger();
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    long start = System.currentTimeMillis();

    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new ClientOneThread(latch, successCount));
    }
    latch.await();
    threadPool.shutdown();

    long end = System.currentTimeMillis();
    double wallTime = (end - start) / 1000f;
    StatsGenerator.printGeneralStats(successCount.get(), wallTime);
  }
}
