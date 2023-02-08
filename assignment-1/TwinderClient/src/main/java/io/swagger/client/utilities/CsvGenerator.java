package io.swagger.client.utilities;

import com.opencsv.CSVWriter;
import io.swagger.client.model.ResponseRecord;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating CSVs
 */
public final class CsvGenerator {

  private static final String[] RESPONSE_RECORD_HEADER = {"StartTime", "Latency", "RequestType", "ResponseCode"};
  private static final String[] THROUGHPUT_INTERVAL_HEADER = {"Interval(s)", "NumberOfRequests"};

  private CsvGenerator() {
  }

  public static void writeResponseRecordCsv(List<ResponseRecord> responseRecordList, String fileName) {
    List<String []> csvData = convertResponseRecordsToCsvData(responseRecordList);
    try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
      writer.writeAll(csvData);
    }
    catch (
        IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeThroughputIntervalsCsv(int[] throughputIntervals, String fileName) {
    List<String []> csvData = convertThroughputIntervalsToCsvData(throughputIntervals);
    try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
      writer.writeAll(csvData);
    }
    catch (
        IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<String[]> convertResponseRecordsToCsvData(List<ResponseRecord> responseRecords) {
    List<String[]> list = new ArrayList<>();
    list.add(RESPONSE_RECORD_HEADER);
    for (ResponseRecord record : responseRecords) {
      String[] row = {String.valueOf(record.getStartTime()),
          String.valueOf(record.getLatency()),
          record.getRequestType(),
          String.valueOf(record.getResponseCode())};
      list.add(row);
    }
    return list;
  }

  private static List<String[]> convertThroughputIntervalsToCsvData(int[] throughputIntervals) {
    List<String[]> list = new ArrayList<>();
    list.add(THROUGHPUT_INTERVAL_HEADER);
    for (int i = 0; i < throughputIntervals.length; i++) {
      String[] row = {String.valueOf(i), String.valueOf(throughputIntervals[i])};
      list.add(row);
    }
    return list;
  }
}
