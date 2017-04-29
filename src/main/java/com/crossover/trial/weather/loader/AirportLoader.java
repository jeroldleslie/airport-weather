package com.crossover.trial.weather.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 * 
 * @author code test administrator
 */
public class AirportLoader {

  private static final Logger LOGGER = Logger.getLogger(AirportLoader.class.getName());

  /** end point for read queries */
//  private WebTarget query;

  /** end point to supply updates */
  private WebTarget collect;

  /** atomic integer to count total csv records */
  private AtomicInteger totalRec;

  /** atomic integer to count failed records */
  private AtomicInteger failedRec;

  /** atomic integer to count successffull uploaded records */
  private AtomicInteger insertedRec;

  public WebTarget getCollect() {
    return collect;
  }

  public AirportLoader() {
    Client client = ClientBuilder.newClient();
//    query = client.target("http://localhost:9090/query");
    collect = client.target("http://localhost:9090/collect");
  }

  public void upload(InputStream airportDataStream) throws IOException {
    System.out.println("Airport csv data uploading. Please wait for a while uploading...");
    BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));

    totalRec = new AtomicInteger(0);
    insertedRec = new AtomicInteger(0);
    failedRec = new AtomicInteger(0);


    try {
      reader.lines().forEach((line) -> {
        totalRec.incrementAndGet();
        WebTarget path = collect.path("/airport");
        try {
          String json = parseAndGetAirportJsonFromLine(line);
          if (json != "") {
            Response post = path.request().post(Entity.entity(json, "application/json"));
            if (post.getStatus() >= 400) {
              failedRec.incrementAndGet();
            } else {
              insertedRec.incrementAndGet();
            }
            try {
              // allow server to run smoothly
              Thread.sleep(10);
            } catch (InterruptedException e) {
              LOGGER.log(Level.SEVERE, null, e);
            }
          }
        } catch (Exception e) {
          failedRec.incrementAndGet();
        }
      });
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, null, e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, null, e);
        }
      }
    }

    System.out.println("Uploading csv completed.");
    printResult(totalRec.get(), failedRec.get());
  }


  /**
   * Give total record count and failed record count to print results in consol
   * 
   * @param totalRec the total record read from csv
   * @param failedRec the number of records faild to insert
   */
  private void printResult(int totalRec, int failedRec) {

    StringBuilder result = new StringBuilder();
    result.append("Total Records = ").append(totalRec).append("\nInserted Records = ")
        .append(totalRec - failedRec).append("\nFailed or Invalid Records = ").append(failedRec);

    System.out.println(result);
  }

  /**
   * Give raw csv line as string to parse and convert to Airport object
   * 
   * @param line the raw single csv line
   * @return airport object
   */
  private String parseAndGetAirportJsonFromLine(String line) {
    try {
      String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
      StringBuilder builder = new StringBuilder();
      builder.append("{").append("name").append(":").append(values[1]).append(",").append("iata")
          .append(":").append(values[4]).append(",").append("latitude").append(":")
          .append(values[6]).append(",").append("longitude").append(":").append(values[7])
          .append(",").append("city").append(":").append(values[2]).append(",").append("country")
          .append(":").append(values[3]).append(",").append("icao").append(":").append(values[5])
          .append(",").append("altitude").append(":").append(values[8]).append(",")
          .append("timezone").append(":").append(values[9]).append(",").append("dst").append(":")
          .append(values[10]).append("}");

      return builder.toString();
    } catch (Exception e) {
      return "";
    }
  }

  public static void main(String args[]) throws IOException {
    File airportDataFile = new File(args[0]);
    if (!airportDataFile.exists() || airportDataFile.length() == 0) {
      System.err.println(airportDataFile + " is not a valid input");
      System.exit(1);
    }
    AirportLoader al = new AirportLoader();
    al.upload(new FileInputStream(airportDataFile));
    System.exit(0);
  }
}
