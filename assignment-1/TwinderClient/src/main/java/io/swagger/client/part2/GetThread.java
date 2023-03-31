package io.swagger.client.part2;

import static io.swagger.client.constants.EnvironmentConstants.ATTEMPTS;
import static io.swagger.client.constants.EnvironmentConstants.GET_SERVER;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import io.swagger.client.model.ResponseRecord;
import io.swagger.client.utilities.HttpInfoGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetThread implements Runnable {

  private List<ResponseRecord> getRecordList;
  private StatsApi statsApiInstance;
  private MatchesApi matchesApiInstance;

  public GetThread(List<ResponseRecord> getRecordList, StatsApi statsApiInstance, MatchesApi matchesApiInstance){
    this.getRecordList = getRecordList;
    this.statsApiInstance = statsApiInstance;
    this.matchesApiInstance = matchesApiInstance;
  }

  @Override
  public void run() {
    int num = ThreadLocalRandom.current().nextInt(0, 2);
    for (int j = 0; j < 5; j++) {
      if (num == 0) {
        sendStatsRequest(getRecordList, statsApiInstance);
      } else {
        sendMatchesRequest(getRecordList, matchesApiInstance);
      }
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void sendStatsRequest(List<ResponseRecord> getRecordList, StatsApi apiInstance) {
    for (int k = 0; k < ATTEMPTS; k++) {
      try {
        String user = HttpInfoGenerator.randomUser();
        long start = System.currentTimeMillis();
        ApiResponse<MatchStats> res = apiInstance.matchStatsWithHttpInfo(
            user);
        long end = System.currentTimeMillis();
        long latency = end - start;
        if (res.getStatusCode() == 200) {
          ResponseRecord record = new ResponseRecord(start, latency, "GET",
              res.getStatusCode());
          getRecordList.add(record);
//          System.out.println(res.getData());
          break;
        }
      } catch (ApiException e) {
        System.err.println("Exception when calling SwipeApi#swipe");
        e.printStackTrace();
      }
    }
  }

  private void sendMatchesRequest(List<ResponseRecord> getRecordList, MatchesApi apiInstance) {
    for (int k = 0; k < ATTEMPTS; k++) {
      try {
        String user = HttpInfoGenerator.randomUser();
        long start = System.currentTimeMillis();
        ApiResponse<Matches> res = apiInstance.matchesWithHttpInfo(user);
        long end = System.currentTimeMillis();
        long latency = end - start;
        if (res.getStatusCode() == 200) {
          ResponseRecord record = new ResponseRecord(start, latency, "GET",
              res.getStatusCode());
          getRecordList.add(record);
//          System.out.println(res.getData());
          break;
        }
      } catch (ApiException e) {
        System.err.println("Exception when calling SwipeApi#swipe");
        e.printStackTrace();
      }
    }
  }
}
