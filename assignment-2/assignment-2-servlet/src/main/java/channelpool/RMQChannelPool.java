package channelpool;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * A simple RabbitMQ channel pool based on a BlockingQueue implementation
 * Authored by Dr. Ian Gorton
 */
public class RMQChannelPool {

  private final BlockingQueue<Channel> pool;
  private int capacity;
  private RMQChannelFactory factory;


  /**
   * Creates a channel pool with the specified number of channels.
   * @param maxSize the number of channels
   * @param factory the channel factory
   */
  public RMQChannelPool(int maxSize, RMQChannelFactory factory) {
    this.capacity = maxSize;
    pool = new LinkedBlockingQueue<>(capacity);
    this.factory = factory;
    for (int i = 0; i < capacity; i++) {
      Channel chan;
      try {
        chan = this.factory.create();
        pool.put(chan);
      } catch (IOException | InterruptedException ex) {
        Logger.getLogger(RMQChannelPool.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
  }

  /**
   * Takes channel from the pool
   */
  public Channel borrowObject() throws IOException {

    try {
      return pool.take();
    } catch (InterruptedException e) {
      throw new RuntimeException("Error: no channels available" + e.toString());
    }
  }

  /**
   * Gives back channel to the pool
   */
  public void returnObject(Channel channel) throws Exception {
    if (channel != null) {
      pool.add(channel);
    }
  }

  public void close() {
    // pool.close();
  }
}
