package io.swagger.client.part2;

import static io.swagger.client.constants.EnvironmentConstants.ATTEMPTS;
import static io.swagger.client.constants.EnvironmentConstants.AWS_PATH;
import static io.swagger.client.constants.EnvironmentConstants.NUM_REQUESTS;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.ResponseRecord;
import io.swagger.client.model.SwipeDetails;
import io.swagger.client.utilities.HttpInfoGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Thread that sends the number of requests specified. It retrieves the
 * responses in its own list, and then once all have been recorded, adds them
 * all to the global response record list.
 */
public class ClientTwoThread implements Runnable {
  private CountDownLatch latch;
  private List<ResponseRecord> responseRecordList;

  public ClientTwoThread(CountDownLatch latch, List<ResponseRecord> responseRecordList) {
    this.latch = latch;
    this.responseRecordList = responseRecordList;
  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(AWS_PATH);
    SwipeApi apiInstance = new SwipeApi(apiClient);
    List<ResponseRecord> localSublist = Collections.synchronizedList(new ArrayList<>());
    for (int j = 0; j < NUM_REQUESTS; j++) {
      sendRequest(localSublist, apiInstance);
    }
    responseRecordList.addAll(localSublist);
    latch.countDown();
  }

  /**
   * Tries sending a request (max 5 attempts) with the requisite http info and
   * adds a record of the response to the local list if successful
   * @param localSublist the local list in the thread
   * @param apiInstance the swipe api instance
   */
  private void sendRequest(List<ResponseRecord> localSublist, SwipeApi apiInstance) {
    for (int k = 0; k < ATTEMPTS; k++) {
      try {
        String leftOrRight = HttpInfoGenerator.returnLeftOrRight();
        SwipeDetails swipeDetails = HttpInfoGenerator.generateSwipeDetails();
        long start = System.currentTimeMillis();
        ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(swipeDetails,
            leftOrRight);
        long end = System.currentTimeMillis();
        long latency = end - start;
        if (res.getStatusCode() == 201) {
          ResponseRecord record = new ResponseRecord(start, latency, "POST",
              res.getStatusCode());
          localSublist.add(record);
          break;
        }
      } catch (ApiException e) {
        System.err.println("Exception when calling SwipeApi#swipe");
        e.printStackTrace();
      }
    }
  }
}

