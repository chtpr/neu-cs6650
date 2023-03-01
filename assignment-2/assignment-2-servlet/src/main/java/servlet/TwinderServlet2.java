package servlet;

import channelpool.RMQChannelFactory;
import channelpool.RMQChannelPool;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.Swipe;
import model.SwipeDetails;

@WebServlet(name = "TwinderServlet2", value = "/*")
public class TwinderServlet2 extends HttpServlet {

  private static final String QUEUE_NAME = "SwipeQueue";
  private static final String EXCHANGE_NAME = "SwipeExchange";
  private static final int NUM_CHANNELS = 100;
  private static final String LOCAL_HOST = "localhost";
  private static final String AWS_PUBLIC = "35.88.142.170";
  private static final String AWS_PRIVATE = "172.31.25.91";
  private Connection connection;
  private RMQChannelPool pool;
  private static final Gson gson = new Gson();



  @Override
  public void init() {
    ConnectionFactory connectionFactory = new ConnectionFactory();
//    connectionFactory.setHost(LOCAL_HOST);
    connectionFactory.setHost(AWS_PRIVATE);
    connectionFactory.setUsername("test");
    connectionFactory.setPassword("test");
    try {
      connection = connectionFactory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
    RMQChannelFactory chanFactory = new RMQChannelFactory (connection);
    pool = new RMQChannelPool(NUM_CHANNELS, chanFactory);
  }

  @Override
  public void destroy() {
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req,
      HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("text/plain");
    // should be /swipe/leftorright/
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    SwipeDetails swipeDetails = parseSwipeDetails(req);

    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts) || !isRequestBodyValid(swipeDetails)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_CREATED);
      // do any sophisticated processing with urlParts which contains all the url params
      try {
        processRequest(res, urlParts[2], swipeDetails);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private SwipeDetails parseSwipeDetails(HttpServletRequest request)
      throws IOException {
    return gson.fromJson(request.getReader(), SwipeDetails.class);
  }

  /**
   * Checks if url is valid. urlParts[0] should be "", urlParts[1] should be
   * "swipe", and urlParts[2] should be "left" or "right"
   * @param urlParts the url parts
   * @return true if url is valid, false if not
   */
  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length != 3) {
      return false;
    }
    if (!Objects.equals(urlParts[1], "swipe")) {
      return false;
    }
    Pattern leftOrRight = Pattern.compile("^left|right$");
    Matcher matcher = leftOrRight.matcher(urlParts[2]);
    return matcher.find();
  }

  private boolean isRequestBodyValid(SwipeDetails swipeDetails) {
    return (checkRange(swipeDetails.getSwiper(), 0, 5000) &&
        checkRange(swipeDetails.getSwipee(), 0, 1000000) &&
        swipeDetails.getComment().length() <= 256);
  }

  private boolean checkRange(String id, int lower, int upper) {
    int value = Integer.parseInt(id);
    return (lower <= value && value <= upper);
  }

  /**
   * Reads the request body and publishes it to RabbitMQ. Writes back the status
   * if successful.
   * @param response the HTTP response that writes back the status code
   * @param swipeDirection the swipe direction
   * @param swipeDetails the swipe details
   */
  private void processRequest(HttpServletResponse response, String swipeDirection, SwipeDetails swipeDetails)
      throws IOException {
    Swipe swipe = new Swipe(swipeDirection, swipeDetails.getSwiper(), swipeDetails.getSwipee(), swipeDetails.getComment());
    String swipeJson = gson.toJson(swipe);
    publish(swipeJson);
    response.getWriter().write(String.valueOf(response.getStatus()));
  }

  private void publish(String message) {
    try {
      Channel channel = pool.borrowObject();
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
      channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
      pool.returnObject(channel);
    } catch (Exception e) {
      Logger.getLogger(TwinderServlet2.class.getName()).log(Level.INFO, null, e);
    }
  }
}
