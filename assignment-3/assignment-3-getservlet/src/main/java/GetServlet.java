import com.google.gson.Gson;
import dao.SwipeDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.LikesDislikes;
import model.MatchList;

@WebServlet(name = "GetServlet", value = "/*")
public class GetServlet extends HttpServlet {

  private static final Gson gson = new Gson();
  private static final SwipeDao swipeDao = new SwipeDao();

  @Override
  protected void doGet(HttpServletRequest req,
      HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("text/plain");
    // should be /matches/userid/ or /stats/userid/
    String urlPath = req.getPathInfo();

    // check we have a URL
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters");
      return;
    }

    // parse the url path and request body
    String[] urlParts = urlPath.split("/");

    // validate url path and request body
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      // if valid, attempt to publish message to RabbitMQ
      // with swipe direction included in the message
    } else {
      int userID = Integer.parseInt(urlParts[2]);
      res.setContentType("application/json");
      if (Objects.equals(urlParts[1], "matches")) {
//        List<String> matchList = new ArrayList<>();
//        matchList.add("4");
//        matchList.add("5");
//        MatchList list = new MatchList(userID, matchList);
//        res.getWriter().write(gson.toJson(list));
        swipeDao.getMatchList(userID, res, gson);

      } else {
//        LikesDislikes likesDislikes = new LikesDislikes(userID,20, 10);
//        res.getWriter().write(gson.toJson(likesDislikes));
        swipeDao.getLikesAndDislikes(userID, res, gson);
      }
      res.setStatus(HttpServletResponse.SC_OK);
    }
  }

  /**
   * Checks if url is valid. urlParts[0] should be "", urlParts[1] should be
   * "matches" or "stats", and urlParts[2] should be "userid"
   * @param urlParts the url parts
   * @return true if url is valid, false if not
   */
  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length != 3) {
      return false;
    }
    if (!Objects.equals(urlParts[1], "matches") && !Objects.equals(urlParts[1], "stats")) {
      return false;
    }
    int value = Integer.parseInt(urlParts[2]);
    return (0 <= value && value <= 5000);
  }

}

