package model;

public class LikesDislikes {

  private String userId;
  private int numLikes;
  private int numDislikes;

  public LikesDislikes(String userId, int numLikes, int numDislikes) {
    this.userId = userId;
    this.numLikes = numLikes;
    this.numDislikes = numDislikes;
  }

  public String getUserId() {
    return userId;
  }

  public int getNumLikes() {
    return numLikes;
  }

  public int getNumDislikes() {
    return numDislikes;
  }
}
