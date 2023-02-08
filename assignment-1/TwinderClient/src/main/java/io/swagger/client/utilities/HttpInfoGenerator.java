package io.swagger.client.utilities;

import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating HTTP info
 */
public final class HttpInfoGenerator {

  private HttpInfoGenerator() {}

  public static String returnLeftOrRight() {
    int num = ThreadLocalRandom.current().nextInt(0, 2);
    if (num == 0) return "left";
    return "right";
  }

  public static SwipeDetails generateSwipeDetails() {
    SwipeDetails swipeDetails = new SwipeDetails();
    int swiperLimit = 5000;
    int swipeeLimit = 1000000;
    swipeDetails.setSwiper(String.valueOf(ThreadLocalRandom.current().nextInt(0, swiperLimit + 1)));
    swipeDetails.setSwipee(String.valueOf(ThreadLocalRandom.current().nextInt(0, swipeeLimit + 1)));
    swipeDetails.setComment(returnRandomString());
    return swipeDetails;
  }

  private static String returnRandomString() {
    int leftLimit = 65; // letter 'A'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 256;
    return ThreadLocalRandom.current().ints(leftLimit, rightLimit + 1)
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
