package model;

public class LikesDislikes {

  private int numLikes;
  private int numDislikes;

  public LikesDislikes(int numLikes, int numDislikes) {
    this.numLikes = numLikes;
    this.numDislikes = numDislikes;
  }

  public int getNumLikes() {
    return numLikes;
  }

  public int getNumDislikes() {
    return numDislikes;
  }
}
