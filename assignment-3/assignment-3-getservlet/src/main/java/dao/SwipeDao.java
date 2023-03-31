package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.MatchStats;
import model.Matches;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Swipe DAO that allows us to query likes, dislikes, and match lists from
 * the database
 */
public class SwipeDao {
  private static BasicDataSource dataSource;

  public SwipeDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  /**
   * Retrieves likes and dislikes from the database for the specified user
   * @param userId the user's ID
   * @return match stats
   */
  public MatchStats getMatchStats(int userId) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    MatchStats matchStats = null;
    String statement = "SELECT COUNT(direction='right' OR null) AS likes, "
        + "COUNT(direction='left' OR null) AS dislikes "
        + "FROM Swipes WHERE swiperId=?";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(statement);
      preparedStatement.setInt(1, userId);
      // execute insert SQL statement

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        matchStats = new MatchStats(String.valueOf(userId),
            rs.getInt("likes"), rs.getInt("dislikes"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }

      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return matchStats;
  }

  /**
   * Retrieves potential matches from the databases for the user specified
   * @param userId the user's ID
   * @return potential matches
   */
  public Matches getMatches(int userId) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    Matches matches = null;
    String statement = "SELECT swipeeId FROM Twinder.Swipes WHERE "
        + "direction='right' AND swiperId=? LIMIT 100";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(statement);
      preparedStatement.setInt(1, userId);
      // execute insert SQL statement

      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        matches = new Matches(String.valueOf(userId), new ArrayList<String>());
        matches.addMatchListItem(String.valueOf(rs.getInt("swipeeId")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return matches;
  }
}
