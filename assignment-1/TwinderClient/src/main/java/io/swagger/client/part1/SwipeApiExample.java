package io.swagger.client.part1;

import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.RandomStringUtils;

public class SwipeApiExample {

  private static final String LOCAL_PATH = "http://localhost:8080/twinderservlet/";
  private static final String AWS_PATH = "http://34.219.45.64:8080/twinderservlet/";
  private static final int NUM_THREADS = 100;
  private static final int NUM_REQUESTS = 5000;
  private static final int TOTAL_REQUESTS = NUM_REQUESTS * NUM_THREADS;
  private static final int ATTEMPTS = 5;
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(NUM_THREADS);
    AtomicInteger successCount = new AtomicInteger();
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    long start = System.currentTimeMillis();
    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(newRunnable(successCount, completed));
    }

    completed.await();
    threadPool.shutdown();

    long end = System.currentTimeMillis();
    double duration = (end - start) / 1000f;
    System.out.printf("Wall time: %f seconds%n", duration);
    System.out.printf("Number of successful requests: %d%n", successCount.get());
    System.out.printf("Number of unsuccessful requests: %d%n", (TOTAL_REQUESTS - successCount.get()));
    System.out.printf("Requests per second: %f%n", (TOTAL_REQUESTS / duration));
  }

  private static String returnLeftOrRight() {
    int num = ThreadLocalRandom.current().nextInt(0, 2);
    if (num == 0) return "left";
    return "right";
  }
  private static SwipeDetails generateSwipeDetails() {
    SwipeDetails swipeDetails = new SwipeDetails();
    swipeDetails.setSwiper(String.valueOf(ThreadLocalRandom.current().nextInt(0, 5001)));
    swipeDetails.setSwipee(String.valueOf(ThreadLocalRandom.current().nextInt(0, 1000001)));
    swipeDetails.setComment(returnRandomString());
    return swipeDetails;
  }

  private static String returnRandomString() {
    return RandomStringUtils.randomAlphabetic(256);
  }

  private static Runnable newRunnable(AtomicInteger successCount, CountDownLatch latch) {
    return new Runnable() {
      @Override
      public void run() {
        ApiClient apiClient = new ApiClient();
        //apiClient.setBasePath(LOCAL_PATH);
        apiClient.setBasePath(AWS_PATH);
        SwipeApi apiInstance = new SwipeApi(apiClient);

        for (int i = 0; i < NUM_REQUESTS; i++) {
          for (int k = 0; k < ATTEMPTS; k++) {
            try {
              String leftOrRight = returnLeftOrRight();
              SwipeDetails randomBody = generateSwipeDetails();
              ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(randomBody,
                  leftOrRight);
              if (res.getStatusCode() == 201) {
                successCount.getAndIncrement();
                break;
              }
            } catch (ApiException e) {
              System.err.println("Exception when calling SwipeApi#swipe");
              e.printStackTrace();
            }
          }
        }
        latch.countDown();
      }
    };
  }
}
