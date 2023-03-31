package model;

/**
 * Model for match stats (likes and dislikes)
 */
public class MatchStats {

  private String userId;
  private int numLikes;
  private int numDislikes;

  public MatchStats(String userId, int numLikes, int numDislikes) {
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
