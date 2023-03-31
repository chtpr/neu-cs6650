package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import dao.SwipeDao;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import model.Swipe;

/**
 * Thread class for the consumer that takes the message from RabbitMQ and sends
 * it to the database
 */
public class ConsumerThread implements Runnable {

  private static final String QUEUE_NAME = "LikesDislikes";
  private static final String EXCHANGE_NAME = "SwipeExchange";

  private Connection connection;
  private SwipeDao swipeDao;

  private static final Gson gson = new Gson();

  public ConsumerThread(Connection connection, SwipeDao swipeDao) {
    this.connection = connection;
    this.swipeDao = swipeDao;
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
    final Channel channel = connection.createChannel();
    boolean autoAck = false;
    boolean durable = true;
    boolean exclusive = false;
    boolean autoDelete = false;
    AtomicBoolean multipleAcks = new AtomicBoolean(false);
    List<Swipe> swipes = Collections.synchronizedList(new ArrayList<>());
    channel.basicQos(100);
    channel.queueDeclare(QUEUE_NAME, durable, exclusive, autoDelete, null);
    channel.exchangeDeclare(EXCHANGE_NAME, "direct", durable);
    // last parameter for queueBind is routingKey
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      Swipe swipe = gson.fromJson(message, Swipe.class);
      swipes.add(swipe);
      if (swipes.size() >= 100) {
        addListToSwipeData(swipes);
        multipleAcks.set(true);
        swipes.clear();
      } else {
        multipleAcks.set(false);
      }
      long deliveryTag = delivery.getEnvelope().getDeliveryTag();
      channel.basicAck(deliveryTag, multipleAcks.get());
    };
    channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
  }

  /**
   * Adds a single swipe to the database
   * @param swipe the swipe object
   */
  private void addSingleToSwipeData(Swipe swipe) {
    swipeDao.createSwipe(swipe);
//    System.out.println(swipe.getSwipeDirection() + " " + swipe.getSwiper() + " " + swipe.getSwipee());
  }

  /**
   * Adds a list of swipes to the database
   * @param swipes the swipe list
   */
  private void addListToSwipeData(List<Swipe> swipes) {
    swipeDao.createSwipes(swipes);
  }
}
