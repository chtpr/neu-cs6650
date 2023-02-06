package io.swagger.client.part1;

import static io.swagger.client.constants.EnvironmentConstants.ATTEMPTS;
import static io.swagger.client.constants.EnvironmentConstants.AWS_PATH;
import static io.swagger.client.constants.EnvironmentConstants.NUM_REQUESTS;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import io.swagger.client.utilities.HttpInfoGenerator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientOneThread implements Runnable {
  private CountDownLatch latch;
  private AtomicInteger successCount;

  public ClientOneThread(CountDownLatch latch, AtomicInteger successCount) {
    this.latch = latch;
    this.successCount = successCount;
  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(AWS_PATH);
    SwipeApi apiInstance = new SwipeApi(apiClient);
    for (int j = 0; j < NUM_REQUESTS; j++) {
      sendRequest(successCount, apiInstance);
    }
    latch.countDown();
  }

  private void sendRequest(AtomicInteger successCount, SwipeApi apiInstance) {
    for (int k = 0; k < ATTEMPTS; k++) {
      try {
        String leftOrRight = HttpInfoGenerator.returnLeftOrRight();
        SwipeDetails swipeDetails = HttpInfoGenerator.generateSwipeDetails();
        ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(swipeDetails,
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
}
