package io.swagger.client.utilities;

import static io.swagger.client.constants.EnvironmentConstants.NUM_THREADS;
import static io.swagger.client.constants.EnvironmentConstants.TOTAL_REQUESTS;

import io.swagger.client.model.ResponseRecord;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Utility class for generating and printing stats
 */
public final class StatsGenerator {

  private StatsGenerator() {}

  public static void printGeneralStats(int successfulRequests, double wallTime) {
    System.out.printf("Number of threads: %d%n", NUM_THREADS);
    System.out.printf("Number of successful requests: %d%n", successfulRequests);
    System.out.printf("Number of unsuccessful requests: %d%n", TOTAL_REQUESTS - successfulRequests);
    System.out.printf("Wall time: %f seconds%n", wallTime);
    System.out.printf("Requests per second: %f%n", TOTAL_REQUESTS / wallTime);
    System.out.println();
  }

  public static void printLatencyStats(List<ResponseRecord> responseRecordList) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (ResponseRecord record : responseRecordList) {
      stats.addValue(record.getLatency());
    }

    double mean = stats.getMean();
    double median = stats.getPercentile(50);
    double max = stats.getMax();
    double min = stats.getMin();
    double p99 = stats.getPercentile(99);
    System.out.printf("Mean latency: %f milliseconds%n", mean);
    System.out.printf("Median latency: %f milliseconds%n", median);
    System.out.printf("Max latency: %f milliseconds%n", max);
    System.out.printf("Min latency: %f milliseconds%n", min);
    System.out.printf("99th percentile latency: %f milliseconds%n", p99);
  }
}
