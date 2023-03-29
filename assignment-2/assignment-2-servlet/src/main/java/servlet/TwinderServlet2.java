package servlet;

import channelpool.RMQChannelFactory;
import channelpool.RMQChannelPool;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
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

/**
 * Servlet for our Twinder application
 */
@WebServlet(name = "TwinderServlet2", value = "/*")
public class TwinderServlet2 extends HttpServlet {

  private static final String EXCHANGE_NAME = "SwipeExchange";
  private static final int NUM_CHANNELS = 100;
  private static final String LOCAL_HOST = "localhost";
  private static final String AWS_PUBLIC = "35.88.142.170";
  private static final String AWS_PRIVATE = "172.31.25.91";
  private Connection connection;
  private RMQChannelPool pool;
  private static final Gson gson = new Gson();

  /**
   * Upon initialization of the servlet, a connection is established to
   * RabbitMQ. A RabbitMQ channel pool is implemented to prevent channel
   * turnover.
   */
  @Override
  public void init() {
    ConnectionFactory connectionFactory = new ConnectionFactory();
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

  /**
   * Executes our post method. Validates the URL and the request body. If valid,
   * attempts to publish message to RabbitMQ. If successful, sends a successful
   * response back to the client.
   * @param req the HTTPServlet request
   * @param res the HTTPServlet response
   */
  @Override
  protected void doPost(HttpServletRequest req,
      HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("text/plain");
    // should be /swipe/leftorright/
    String urlPath = req.getPathInfo();

    // check we have a URL
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters");
      return;
    }

    // parse the url path and request body
    String[] urlParts = urlPath.split("/");
    SwipeDetails swipeDetails = gson.fromJson(req.getReader(), SwipeDetails.class);

    // validate url path and request body
    if (!isUrlValid(urlParts) || !isRequestBodyValid(swipeDetails)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      // if valid, attempt to publish message to RabbitMQ
      // with swipe direction included in the message
    } else {
      Swipe swipe = new Swipe(urlParts[2], swipeDetails.getSwiper(),
          swipeDetails.getSwipee(), swipeDetails.getComment());
      String swipeJson = gson.toJson(swipe);
      // if published, return success back to the client
      if (durablePublish(swipeJson)) {
        res.setStatus(HttpServletResponse.SC_CREATED);
        res.getWriter().write(String.valueOf(res.getStatus()));
      } else {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().write("Failed to publish message");
      }
    }
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

  /**
   * Checks if request body is valid.
   * @param swipeDetails the swipe details
   * @return true if request body is valid, false if not
   */
  private boolean isRequestBodyValid(SwipeDetails swipeDetails) {
    return (checkRange(swipeDetails.getSwiper(), 0, 5000) &&
        checkRange(swipeDetails.getSwipee(), 0, 1000000) &&
        swipeDetails.getComment().length() <= 256);
  }

  /**
   * Checks if the user id is in the specified range.
   * @param id the user id
   * @param lower lower bound
   * @param upper upper bound
   * @return true if in range, false if not
   */
  private boolean checkRange(String id, int lower, int upper) {
    int value = Integer.parseInt(id);
    return (lower <= value && value <= upper);
  }

  /**
   * Publishes the swipe message to rabbitMQ
   * @param message the swipe message
   * @return true if message was published, false if not
   */
  private boolean publish(String message) {
    try {
      Channel channel = pool.borrowObject();
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
      channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
      pool.returnObject(channel);
      return true;
    } catch (Exception e) {
      Logger.getLogger(TwinderServlet2.class.getName()).log(Level.INFO, null, e);
      return false;
    }
  }

  private boolean durablePublish(String message) {
    try {
      Channel channel = pool.borrowObject();
      channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
      channel.basicPublish(EXCHANGE_NAME, "",
          new AMQP.BasicProperties.Builder()
              .deliveryMode(2)
              .build(),
          message.getBytes());
      pool.returnObject(channel);
      return true;
    } catch (Exception e) {
      Logger.getLogger(TwinderServlet2.class.getName()).log(Level.INFO, null, e);
      return false;
    }
  }
}
