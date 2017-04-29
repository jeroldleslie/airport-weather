package com.crossover.trial.weather;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.crossover.trial.weather.collect.CollectService;
import com.crossover.trial.weather.collect.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.query.RestWeatherQueryEndpoint;

/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or
 * remove the main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {
  public static final String BASE_URI;
  public static final String protocol;
  public static final Optional<String> host;
  public static final Optional<String> port;

  /** https server */
  static HttpServer server;


  static {
    protocol = "http://";
    host = Optional.ofNullable(System.getenv("HOSTNAME"));
    port = Optional.ofNullable(System.getenv("PORT"));
    BASE_URI = protocol + host.orElse("localhost") + ":" + port.orElse("9090") + "/";
  }

  public static void main(String[] args) throws Exception {
    for (String arg : args) {
      if (arg.trim().equals("--init")) {
        CollectService collectService = new CollectService();
        collectService.clean();
        collectService.addAirport("BOS", "42.364347", "-71.005181");
        collectService.addAirport("EWR", "40.6925", "-74.168667");
        collectService.addAirport("JFK", "40.639751", "-73.778925");
        collectService.addAirport("LGA", "40.777245", "-73.872608");
        collectService.addAirport("MMU", "40.79935", "-74.4148747");
        List<String> list = new ArrayList<String>(Arrays.asList(args));
        list.remove(arg);
        args = list.toArray(new String[0]);
        break;
      }
    }
    try {
      System.out.println("Starting Weather App local testing server: " + BASE_URI);

      final ResourceConfig resourceConfig = new ResourceConfig();
      resourceConfig.register(RestWeatherCollectorEndpoint.class);
      resourceConfig.register(RestWeatherQueryEndpoint.class);

      server =
          GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig, false);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        server.shutdownNow();
      }));

      HttpServerProbe probe = new HttpServerProbe.Adapter() {
        public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection,
            Request request) {}
      };
      server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);

      // the autograder waits for this output before running automated tests,
      // please don't remove it
      server.start();
      System.out.println(format("Weather Server started.\n url=%s\n", BASE_URI));
      // blocks until the process is terminated
      Thread.currentThread().join();
      server.shutdown();
    } catch (IOException | InterruptedException ex) {
      Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static void shutdown() throws Exception {
    try{
      server.shutdownNow();
    }catch(Exception e){
      throw e;
    }
  }
  
}
