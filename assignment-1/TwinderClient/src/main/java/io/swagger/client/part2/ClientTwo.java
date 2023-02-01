package io.swagger.client.part2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.ResponseRecord;
import io.swagger.client.model.ResponseRecord.StartTimeComparator;
import io.swagger.client.model.SwipeDetails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;

public class ClientTwo {

  private static final String LOCAL_PATH = "http://localhost:8080/twinderservlet/";
  private static final String AWS_PATH = "http://34.219.45.64:8080/twinderservlet/";
  private static final int NUM_THREADS = 100;
  private static final int NUM_REQUESTS = 5000;
  private static final int TOTAL_REQUESTS = NUM_REQUESTS * NUM_THREADS;
  private static final int ATTEMPTS = 5;
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(NUM_THREADS);
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    List<ResponseRecord> list = new ArrayList<>();
    List<ResponseRecord> syncList = Collections.synchronizedList(list);
    long start = System.currentTimeMillis();

    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(newRunnable(completed, syncList));
    }

    completed.await();
    threadPool.shutdown();
    long end = System.currentTimeMillis();
    double duration = (end - start) / 1000f;
    System.out.println(syncList.size());
    syncList.sort(new StartTimeComparator());
    System.out.println(syncList.get(0).getStartTime());
    System.out.println(syncList.get(499999).getStartTime());
    System.out.println(syncList.get(499999).getStartTime() - syncList.get(0).getStartTime());

    System.out.printf("Wall time: %f seconds%n", duration);
    System.out.printf("Number of successful requests: %d%n", syncList.size());
    System.out.printf("Number of unsuccessful requests: %d%n", (TOTAL_REQUESTS - syncList.size()));
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

  private ResponseRecord addRecord() {
    return null;
  }

  private static Runnable newRunnable(CountDownLatch latch, List<ResponseRecord> mainList) {
    return new Runnable() {
      @Override
      public void run() {
        ApiClient apiClient = new ApiClient();
        //apiClient.setBasePath(LOCAL_PATH);
        apiClient.setBasePath(AWS_PATH);
        SwipeApi apiInstance = new SwipeApi(apiClient);
        List<ResponseRecord> oneThreadList = new ArrayList<>();
        List<ResponseRecord> oneThreadSyncList = Collections.synchronizedList(oneThreadList);

        for (int i = 0; i < NUM_REQUESTS; i++) {
          for (int k = 0; k < ATTEMPTS; k++) {
            try {
              String leftOrRight = returnLeftOrRight();
              SwipeDetails randomBody = generateSwipeDetails();
              long start = System.currentTimeMillis();
              ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(randomBody,
                  leftOrRight);
              long end = System.currentTimeMillis();
              long duration = (end - start);
              if (res.getStatusCode() == 201) {
                ResponseRecord record = new ResponseRecord(start, "POST", duration,
                    res.getStatusCode());
                oneThreadSyncList.add(record);
                break;
              }
            } catch (ApiException e) {
              System.err.println("Exception when calling SwipeApi#swipe");
              e.printStackTrace();
            }
          }
        }
        mainList.addAll(oneThreadSyncList);
        latch.countDown();
      }
    };
  }
}
