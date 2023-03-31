package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a list of potential matches
 */
public class Matches {

  private String userId;
  private List<String> matchList;

  public Matches(String userId, List<String> matchList) {
    this.userId = userId;
    this.matchList = matchList;
  }

  public String getUserId() {
    return userId;
  }

  public List<String> getMatchList() {
    return matchList;
  }

  public void addMatchListItem(String matchListItem) {
    if (this.matchList == null) {
      this.matchList = new ArrayList<String>();
    }
    this.matchList.add(matchListItem);
  }
}
