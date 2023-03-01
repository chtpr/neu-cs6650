package io.swagger.client.constants;


/**
 * Class for the environment constants. Thought a class would be appropriate to
 * use in this case because the constants between the two parts should never
 * be different.
 */
public final class EnvironmentConstants {
  public static final String LOCAL_PATH = "http://localhost:8080/twinderservlet/";
  public static final String LOCAL_PATH_HW2 = "http://localhost:8080/twinderservlet2/";
  public static final String IP_ADDRESS = "34.219.165.216";
  public static final String IP_ADDRESS_2 = "54.245.140.162";
  public static final String AWS_PATH = String.format("http://%s:8080/twinderservlet/", IP_ADDRESS);
  public static final String AWS_PATH_2 = String.format("http://%s:8080/twinderservlet2/", IP_ADDRESS);
  public static final String SERVER_2 = String.format("http://%s:8080/twinderservlet2/", IP_ADDRESS_2);
  public static final String LOAD_BALANCER = "http://tomcat-network-acff0945bf37a74d.elb.us-west-2.amazonaws.com/twinderservlet2/";
  public static final int NUM_THREADS = 100;
  public static final int NUM_REQUESTS = 5000;
  public static final int TOTAL_REQUESTS = NUM_REQUESTS * NUM_THREADS;
  public static final int ATTEMPTS = 5;
}
