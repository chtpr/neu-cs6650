package dao;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import model.LikesDislikes;
import model.MatchList;
import org.apache.commons.dbcp2.BasicDataSource;

public class SwipeDao {
  private static BasicDataSource dataSource;

  public SwipeDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  public void getLikesAndDislikes(int userId, HttpServletResponse res, Gson gson) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
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
        LikesDislikes likesDislikes = new LikesDislikes(String.valueOf(userId),
            rs.getInt("likes"), rs.getInt("dislikes"));
        res.getWriter().write(gson.toJson(likesDislikes));
      }

    } catch (SQLException | IOException e) {
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

  public void getMatchList(int userId, HttpServletResponse res, Gson gson) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String statement = "SELECT swipeeId FROM Twinder.Swipes WHERE "
        + "direction='right' AND swiperId=? LIMIT 10";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(statement);
      preparedStatement.setInt(1, userId);
      // execute insert SQL statement

      ResultSet rs = preparedStatement.executeQuery();
      List<String> matchList = new ArrayList<>();
      while (rs.next()) {
        matchList.add(String.valueOf(rs.getInt("swipeeId")));
        //System.out.println(rs.getInt("swipeeId"));
      }
      MatchList matchListObject = new MatchList(String.valueOf(userId), matchList);
      res.getWriter().write(gson.toJson(matchListObject));
    } catch (SQLException | IOException e) {
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
