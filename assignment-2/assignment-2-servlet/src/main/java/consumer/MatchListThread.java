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

public class MatchListThread implements Runnable {
  private static final String QUEUE_NAME = "SwipeQueue";
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
//    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
    channel.basicQos(1);
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      addToMatchList(message);
    };
    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
    });

  }

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
    matchList.add(swipe.getSwipee());
    map.put(swipe.getSwiper(), matchList);
//    System.out.println(matchList.get(matchList.size() - 1));
    }
  }
}
