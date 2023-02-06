package io.swagger.client.part2;

import static io.swagger.client.constants.EnvironmentConstants.*;

import io.swagger.client.model.ResponseRecord;
import io.swagger.client.model.ResponseRecord.StartTimeComparator;
import io.swagger.client.utilities.CsvGenerator;
import io.swagger.client.utilities.StatsGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTwo {

  private static final String RECORD_FILENAME = "/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csvresults/responsedata.csv";
  private static final String INTERVAL_FILENAME = "/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csvresults/requestseverysecond.csv";
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(NUM_THREADS);
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    List<ResponseRecord> responseRecordList = Collections.synchronizedList(new ArrayList<>());
    long start = System.currentTimeMillis();

    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new ClientTwoThread(latch, responseRecordList));
    }
    latch.await();
    threadPool.shutdown();

    long end = System.currentTimeMillis();
    double wallTime = (end - start) / 1000f;
    System.out.printf("Wall time: %f seconds%n", wallTime);
    System.out.printf("Number of successful requests: %d%n", responseRecordList.size());
    System.out.printf("Number of unsuccessful requests: %d%n", (TOTAL_REQUESTS - responseRecordList.size()));
    System.out.printf("Requests per second: %f%n", (TOTAL_REQUESTS / wallTime));
    System.out.println();

    responseRecordList.sort(new StartTimeComparator());
    long firstTime = responseRecordList.get(0).getStartTime();
    long lastTime = responseRecordList.get(responseRecordList.size() - 1).getStartTime();
    int[] throughputIntervals = new int[(int) ((lastTime - firstTime)/1000 + 1)];
    for (ResponseRecord record : responseRecordList) {
      throughputIntervals[(int) (record.getStartTime() - firstTime)/1000] += 1;
    }

    System.out.printf("First start time: %d milliseconds%n", firstTime);
    System.out.printf("Final start time: %d milliseconds%n", lastTime);
    System.out.printf("Last and first start time gap: %d milliseconds%n", lastTime - firstTime);
    System.out.printf("Number of second intervals: %d%n", throughputIntervals.length);
    System.out.println();

    StatsGenerator.printStats(responseRecordList);
    CsvGenerator.writeResponseRecordCsv(responseRecordList, RECORD_FILENAME);
    CsvGenerator.writeThroughputIntervalsCsv(throughputIntervals, INTERVAL_FILENAME);
  }
}
