package model;

import java.util.List;

public class MatchList {

  private String userId;
  private List<String> matchList;

  public MatchList(String userId, List<String> matchList) {
    this.userId = userId;
    this.matchList = matchList;
  }

  public String getUserId() {
    return userId;
  }

  public List<String> getMatchList() {
    return matchList;
  }
}
