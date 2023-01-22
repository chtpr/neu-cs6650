package servlet;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "TwinderServlet", value = "/*")
public class TwinderServlet extends HttpServlet {

  // just to quickly test we have connection
  @Override
  protected void doGet(HttpServletRequest req,
      HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");

    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      res.getWriter().write("Get works!");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req,
      HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");

    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_CREATED);
      // do any sophisticated processing with urlParts which contains all the url params
      processRequest(req, res);
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    if (urlPath.length < 2) {
      return false;
    }
    if (!Objects.equals(urlPath[1], "swipe")) {
      return false;
    }
    Pattern leftOrRight = Pattern.compile("^left|right$");
    Matcher matcher = leftOrRight.matcher(urlPath[2]);
    return matcher.find();
  }

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    StringBuilder sb = new StringBuilder();
    String str;
    while ((str = request.getReader().readLine()) != null) {
      sb.append(str);
    }
    response.getWriter().write(sb.toString());
  }
}
