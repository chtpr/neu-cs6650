import com.google.gson.Gson;
import dao.SwipeDao;
import java.util.Objects;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

/**
 * Servlet for get requests that are sent to the database
 */
@WebServlet(name = "GetServlet", value = "/*")
public class GetServlet extends HttpServlet {

  private static final Gson gson = new Gson();
  private static final SwipeDao swipeDao = new SwipeDao();

  /**
   * Executes our get method. Validates the URL and the request body. If valid,
   * retrieves data from the database and sends a success response back to the
   * client.
   * @param req the HTTPServlet request
   * @param res the HTTPServlet response
   */
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
      // if valid, depending on which API specified, will retrieve either
      // the match list or the match stats from the database
    } else {
      int userId = Integer.parseInt(urlParts[2]);
      res.setContentType("application/json");
      if (Objects.equals(urlParts[1], "matches")) {
        res.getWriter().write(gson.toJson(swipeDao.getMatches(userId)));
      } else {
        res.getWriter().write(gson.toJson(swipeDao.getMatchStats(userId)));
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

