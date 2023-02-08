package io.swagger.client.model;

import java.util.Comparator;

/**
 * Class for keeping a record of the start time, latency, request type, and
 * response code of the response.
 */
public class ResponseRecord {
  private long startTime;
  private String requestType;
  private long latency;
  private int responseCode;

  public ResponseRecord(long startTime, long latency, String requestType, int responseCode) {
    this.startTime = startTime;
    this.latency = latency;
    this.requestType = requestType;
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
