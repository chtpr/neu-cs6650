package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import model.Swipe;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Swipe DAO that allows us to insert swipe records into the database
 */
public class SwipeDao {
  private static BasicDataSource dataSource;

  public SwipeDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  /**
   * Inserts a single swipe into the database
   * @param swipe the swipe object
   */
  public void createSwipe(Swipe swipe) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO Swipes (direction, swiperId, swipeeId) " +
        "VALUES (?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setString(1, swipe.getSwipeDirection());
      preparedStatement.setInt(2, Integer.parseInt(swipe.getSwiper()));
      preparedStatement.setInt(3, Integer.parseInt(swipe.getSwipee()));
      // execute insert SQL statement
      preparedStatement.executeUpdate();
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
  }

  /**
   * Inserts a list of swipes in a single batch to the database
   * @param swipes list of swipes
   */
  public void createSwipes(List<Swipe> swipes) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO Swipes (direction, swiperId, swipeeId) " +
        "VALUES (?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      for (Swipe swipe : swipes) {
        preparedStatement.setString(1, swipe.getSwipeDirection());
        preparedStatement.setInt(2, Integer.parseInt(swipe.getSwiper()));
        preparedStatement.setInt(3, Integer.parseInt(swipe.getSwipee()));
        preparedStatement.addBatch();
      }
      // execute insert SQL statement
      preparedStatement.executeBatch();
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
  }
}
