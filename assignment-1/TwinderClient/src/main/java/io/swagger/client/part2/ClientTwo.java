package io.swagger.client.part2;

import com.opencsv.CSVWriter;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.ResponseRecord;
import io.swagger.client.model.ResponseRecord.StartTimeComparator;
import io.swagger.client.model.SwipeDetails;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ClientTwo {

  private static final String LOCAL_PATH = "http://localhost:8080/twinderservlet/";
  private static final String AWS_PATH = "http://54.149.203.121:8080/twinderservlet/";
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
    syncList.sort(new StartTimeComparator());
    System.out.println(syncList.get(0).getStartTime());
    System.out.println(syncList.get(syncList.size() - 1).getStartTime());
    System.out.println(syncList.get(syncList.size() - 1).getStartTime() - syncList.get(0).getStartTime());

    System.out.printf("Wall time: %f seconds%n", duration);
    System.out.printf("Number of successful requests: %d%n", syncList.size());
    System.out.printf("Number of unsuccessful requests: %d%n", (TOTAL_REQUESTS - syncList.size()));
    System.out.printf("Requests per second: %f%n", (TOTAL_REQUESTS / duration));

    List <String[]> responseData = convertToCsvData(syncList);
    try (CSVWriter writer = new CSVWriter(new FileWriter("/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csv/responsedata.csv"))) {
      writer.writeAll(responseData);
    }
    catch (
        IOException e) {
      throw new RuntimeException(e);
    }

    long firstTime = syncList.get(0).getStartTime();
    long lastTime = syncList.get(syncList.size() - 1).getStartTime();
    int[] requestsForEverySecond = new int[(int) (lastTime - firstTime)/1000 + 1];
    System.out.println(requestsForEverySecond.length);
    for (ResponseRecord record : syncList) {
      requestsForEverySecond[(int) (record.getStartTime() - firstTime)/1000] += 1;
    }
//    for (int interval : intervals) {
//      System.out.println(interval);
//    }

    List <String[]> requestsForEverySecondCsv = convertToCsvData(requestsForEverySecond);
    try (CSVWriter writer = new CSVWriter(new FileWriter("/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csv/requestseverysecond.csv"))) {
      writer.writeAll(requestsForEverySecondCsv);
    }
    catch (
        IOException e) {
      throw new RuntimeException(e);
    }

    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (ResponseRecord record : syncList) {
      stats.addValue(record.getLatency());
    }

    double mean = stats.getMean();
    double median = stats.getPercentile(50);
    double max = stats.getMax();
    double min = stats.getMin();
    double p99 = stats.getPercentile(99);
    System.out.printf("Mean latency: %f milliseconds%n", mean);
    System.out.printf("Median latency: %f milliseconds%n", median);
    System.out.printf("Max latency: %f milliseconds%n", max);
    System.out.printf("Min latency: %f milliseconds%n", min);
    System.out.printf("99th percentile latency: %f milliseconds%n", p99);
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

  private static List<String[]> convertToCsvData(List<ResponseRecord> responseRecords) {
    List<String[]> list = new ArrayList<>();
    String[] header = {"StartTime", "Latency", "RequestType", "ResponseCode"};
    list.add(header);
    for (ResponseRecord record : responseRecords) {
      String[] row = {String.valueOf(record.getStartTime()),
          String.valueOf(record.getLatency()),
          record.getRequestType(),
          String.valueOf(record.getResponseCode())};
      list.add(row);
    }
    return list;
  }

  private static List<String[]> convertToCsvData(int[] intervals) {
    List<String[]> list = new ArrayList<>();
    String[] header = {"Interval(s)", "NumberOfRequests"};
    list.add(header);
    for (int i = 0; i < intervals.length; i++) {
      String[] row = {String.valueOf(i), String.valueOf(intervals[i])};
      list.add(row);
    }
    return list;
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
                ResponseRecord record = new ResponseRecord(start, duration, "POST",
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
