package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import model.Swipe;

/**
 * Thread class for the match list consumer
 */
public class MatchListThread implements Runnable {
  private static final String QUEUE_NAME = "MatchesQueue";
  private static final String EXCHANGE_NAME = "SwipeExchange";

  private Connection connection;
  private ConcurrentHashMap<String, List<String>> map;

  public MatchListThread(Connection connection, ConcurrentHashMap<String, List<String>> map) {
    this.connection = connection;
    this.map = map;
  }

  @Override
  public void run() {
    try {
      consume();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void consume() throws IOException {
    Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      addToMatchList(message);
    };
    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
    });

  }

  /**
   * Adds to a swiper's match list (users that the swiper has swiped right on)
   * in a hash map. The match list only holds up to 100 matches
   * @param message the message with the swipe info that we get from RabbitMQ
   */
  private void addToMatchList(String message) {
    Gson gson = new Gson();
    Swipe swipe = gson.fromJson(message, Swipe.class);
    if (swipe.getSwipeDirection().equals("right")) {
      List<String> matchList;
      if (!map.containsKey(swipe.getSwiper())) {
        matchList = new ArrayList<>();
      } else {
        matchList = map.get(swipe.getSwiper());
      }
    if (matchList.size() > 100) {
      return;
    }
    matchList.add(swipe.getSwipee());
    map.put(swipe.getSwiper(), matchList);
    }
  }
}
