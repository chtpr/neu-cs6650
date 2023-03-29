package model;

/**
 * Model for a user's swipe info that includes the direction
 */
public class Swipe {

  private String swipeDirection;
  private String swiper;
  private String swipee;
  private String comment;

  public Swipe(String swipeDirection, String swiper, String swipee, String comment) {
    this.swipeDirection = swipeDirection;
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  public String getSwipeDirection() {
    return swipeDirection;
  }

  public String getSwiper() {
    return swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public String getComment() {
    return comment;
  }
}
