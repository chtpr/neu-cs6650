package io.swagger.client.constants;


/**
 * Class for the environment constants. Thought a class would be appropriate to
 * use in this case because the constants between the two parts should never
 * be different.
 */
public final class EnvironmentConstants {
  public static final String LOCAL_PATH = "http://localhost:8080/twinderservlet/";
  public static final String LOCAL_PATH_HW2 = "http://localhost:8080/twinderservlet2/";
  public static final String IP_ADDRESS = "52.39.112.106";
  public static final String AWS_PATH = String.format("http://%s:8080/twinderservlet/", IP_ADDRESS);
  public static final String AWS_PATH_2 = String.format("http://%s:8080/twinderservlet2/", IP_ADDRESS);
  public static final int NUM_THREADS = 100;
  public static final int NUM_REQUESTS = 5000;
  public static final int TOTAL_REQUESTS = NUM_REQUESTS * NUM_THREADS;
  public static final int ATTEMPTS = 5;
}
