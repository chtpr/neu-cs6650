package io.swagger.client.model;

import java.util.Comparator;

public class ResponseRecord {
  private long startTime;
  private String requestType;
  private long latency;
  private int responseCode;

  public ResponseRecord(long startTime, String requestType, long latency,
      int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public long getLatency() {
    return latency;
  }

  public void setLatency(long latency) {
    this.latency = latency;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public static class StartTimeComparator implements Comparator<ResponseRecord> {

    @Override
    public int compare(ResponseRecord o1, ResponseRecord o2) {
      return Long.compare(o1.getStartTime(), o2.getStartTime());
    }
  }
}
