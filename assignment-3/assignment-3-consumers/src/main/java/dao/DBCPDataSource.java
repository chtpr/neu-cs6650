package dao;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Establishes connection to the database
 */
public class DBCPDataSource {
  private static BasicDataSource dataSource;

  // NEVER store sensitive information below in plain text!
  private static final String HOST_NAME = "localhost";
  private static final String AWS = "database-3.ciiihiypfrfb.us-west-2.rds.amazonaws.com";
  private static final String PORT = "3306";
  private static final String PORT_AWS = "3315";
  private static final String DATABASE = "Twinder";
  private static final String USERNAME = "root";
  private static final String ADMIN = "admin";
  private static final String AWS_PASSWORD = "Twinder1!";
  private static final String PASSWORD = "password";

  static {
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    dataSource = new BasicDataSource();
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC&rewriteBatchedStatements=true", AWS, PORT_AWS, DATABASE);
    dataSource.setUrl(url);
    dataSource.setUsername(ADMIN);
    dataSource.setPassword(AWS_PASSWORD);
    dataSource.setInitialSize(10);
    dataSource.setMaxTotal(30);
  }

  public static BasicDataSource getDataSource() {
    return dataSource;
  }
}

