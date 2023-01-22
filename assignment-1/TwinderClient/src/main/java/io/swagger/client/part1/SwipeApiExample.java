package io.swagger.client.part1;

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;

import java.io.File;
import java.util.*;

public class SwipeApiExample {

  public static void main(String[] args) {

    ApiClient apiClient = new ApiClient();
    //local
    apiClient.setBasePath("http://localhost:8080/twinderservlet/");
    //aws
    //apiClient.setBasePath("http://34.216.124.230:8080/Lab2_war_exploded/");
    SwipeApi apiInstance = new SwipeApi(apiClient);
    SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
    body.setSwiper("1");
    body.setSwipee("5");
    body.setComment("Rejected");
    String leftorright = "right"; // String | Ilike or dislike user
    long start = System.currentTimeMillis();
    for (int i = 0; i < 100; i++) {
      try {
        apiInstance.swipe(body, leftorright);
//        ApiResponse<Void> res = apiInstance.swipeWithResponseBody(body,
//            leftorright);
//        System.out.println(res.getStatusCode());
//        System.out.println(res.getHeaders());
//        System.out.println(res.getData());
      } catch (ApiException e) {
        System.err.println("Exception when calling SwipeApi#swipe");
        e.printStackTrace();
      }
    }
    long end = System.currentTimeMillis();
    long duration = end - start;
    System.out.println(duration);
  }
}
