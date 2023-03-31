package io.swagger.client.part2;

import static io.swagger.client.constants.EnvironmentConstants.*;

import io.swagger.client.ApiClient;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.ResponseRecord;
import io.swagger.client.model.ResponseRecord.StartTimeComparator;
import io.swagger.client.utilities.StatsGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client for part 2
 */
public class ClientTwo {

  private static final String RECORD_FILENAME = "/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csvresults/responsedata.csv";
  private static final String INTERVAL_FILENAME = "/Users/christopherlee/NEU/neu-cs6650/assignment-1/TwinderClient/csvresults/requestseverysecond.csv";
  public static void main(String[] args) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(NUM_THREADS);
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    List<ResponseRecord> responseRecordList = Collections.synchronizedList(new ArrayList<>());
    long start = System.currentTimeMillis();

    // Post requests
    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new ClientTwoThread(latch, responseRecordList));
    }

    // Get requests
    boolean flag = true;
    List<ResponseRecord> getRecordList = new ArrayList<>();
    ApiClient getApiClient = new ApiClient();
    getApiClient.setBasePath(GET_SERVER);
    StatsApi statsApiInstance = new StatsApi(getApiClient);
    MatchesApi matchesApiInstance = new MatchesApi(getApiClient);
    GetThread getThread = new GetThread(getRecordList, statsApiInstance, matchesApiInstance);
    while (flag) {
      if (latch.getCount() == 0) {
        flag = false;
      } else {
        getThread.run();
      }
    }

    latch.await();
    threadPool.shutdown();

    long end = System.currentTimeMillis();
    double wallTime = (end - start) / 1000f;
    StatsGenerator.printGeneralStats(responseRecordList.size(), wallTime);
    StatsGenerator.printLatencyStats(responseRecordList);
    StatsGenerator.printLatencyStats(getRecordList);

    responseRecordList.sort(new StartTimeComparator());
    long firstTime = responseRecordList.get(0).getStartTime();
    long lastTime = responseRecordList.get(responseRecordList.size() - 1).getStartTime();
    // create an array with a size equivalent to the number of seconds elapsed
    // plus one to account for index starting at zero
    int[] throughputIntervals = new int[(int) ((lastTime - firstTime) / 1000
        + 1)];
    // uses the start time of every response record to count the number of requests
    // for each second of the run
    for (ResponseRecord record : responseRecordList) {
      throughputIntervals[(int)(record.getStartTime() - firstTime) / 1000] += 1;
    }
//    CsvGenerator.writeResponseRecordCsv(responseRecordList, RECORD_FILENAME);
//    CsvGenerator.writeThroughputIntervalsCsv(throughputIntervals, INTERVAL_FILENAME);
  }
}
