package com.crossover.trial.weather.loader;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Set;

import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.WeatherServer;
import com.crossover.trial.weather.collect.AirportData;
import com.google.gson.Gson;

public class AirportLoaderTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private Gson _gson = new Gson();
  PrintStream out;
  PrintStream error;

  WeatherServer weatherServer;
  AirportLoader al;
  Thread serverThread;
  InputStream inputStream;


  final Runnable runnable = new Runnable() {
    @Override
    public void run() {
      try {
        String[] args = {};
        WeatherServer.main(args);
      } catch (Exception e) {
        System.exit(1);
      }
    }
  };


  @Before
  public void setUp() throws InterruptedException, IOException {
    out = new PrintStream(outContent);
    error = new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);

    al = new AirportLoader();
    serverThread = new Thread(runnable);
    serverThread.start();

    while (true) {
      if (outContent.toString().contains("Weather Server started.")) {
        break;
      } else if (outContent.toString().contains("Address already in use")) {
        System.exit(1);
      }
      Thread.sleep(100);
    }

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("airports.dat").getFile());
    System.out.println(file.getAbsolutePath());
    inputStream = new FileInputStream(file);

    al.upload(inputStream);
  }

  @After
  public void tearDown() throws Exception {
    inputStream.close();
    WeatherServer.shutdown();
    serverThread.stop();
    out = null;
    error = null;
    inputStream = null;
    System.setOut(System.out);
    System.setErr(System.err);
  }

  @Test
  public void testUpload() throws Exception {
    WebTarget path = al.getCollect().path("/airports");
    Set<String> retval = path.request().get().readEntity(Set.class);
    assertEquals(10, retval.size());
  }

  @Test
  public void testUploadAndGetAirport() throws Exception {
    System.setOut(System.out);
    WebTarget path = al.getCollect().path("/airport/MMU");
    String json = path.request().get().readEntity(String.class);

    AirportData ad = _gson.fromJson(json, AirportData.class);
    assertEquals("Morristown Municipal Airport", ad.getName());
  }

}
