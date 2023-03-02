package model;

/**
 * Model for a user's swipe details. Does not include direction
 */
public class SwipeDetails {

  private String swiper;
  private String swipee;
  private String comment;

  public SwipeDetails(String swiper, String swipee, String comment) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
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
