package io.swagger.client.part1;

import static io.swagger.client.constants.EnvironmentConstants.NUM_THREADS;
import static io.swagger.client.constants.EnvironmentConstants.TOTAL_REQUESTS;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
    System.out.printf("Wall time: %f seconds%n", wallTime);
    System.out.printf("Number of successful requests: %d%n", successCount.get());
    System.out.printf("Number of unsuccessful requests: %d%n", (TOTAL_REQUESTS - successCount.get()));
    System.out.printf("Requests per second: %f%n", (TOTAL_REQUESTS / wallTime));
  }
}
