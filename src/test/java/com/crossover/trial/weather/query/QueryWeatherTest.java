package com.crossover.trial.weather.query;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.WeatherCollectorEndpoint;
import com.crossover.trial.weather.WeatherQueryEndpoint;
import com.crossover.trial.weather.collect.AtmosphericInformation;
import com.crossover.trial.weather.collect.CollectService;
import com.crossover.trial.weather.collect.DataPoint;
import com.crossover.trial.weather.collect.RestWeatherCollectorEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class QueryWeatherTest {

  private WeatherQueryEndpoint _query;

  private WeatherCollectorEndpoint _update;

  private CollectService collectService = new CollectService();
  private Gson _gson = new Gson();

  private DataPoint _dp;

  @Before
  public void setUp() throws Exception {
    _query = new RestWeatherQueryEndpoint();
    _update = new RestWeatherCollectorEndpoint();
    collectService.clean();
    collectService.addAirport("BOS", "42.364347", "-71.005181");
    collectService.addAirport("EWR", "40.6925", "-74.168667");
    collectService.addAirport("JFK", "40.639751", "-73.778925");
    collectService.addAirport("LGA", "40.777245", "-73.872608");
    collectService.addAirport("MMU", "40.79935", "-74.4148747");
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
    _query.weather("BOS", "0").getEntity();
  }

  @After
  public void tearDown() {
    collectService.clean();
    _query = null;
    _update = null;
    _dp = null;
  }

  @Test
  public void testPing() throws Exception {
    String ping = _query.ping();
    JsonElement pingResult = new JsonParser().parse(ping);
    assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
    assertEquals(5,
        pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
  }

  @Test
  public void testWeatherGet() throws Exception {
    Response response = _query.weather("BOS", "0");
    List<AtmosphericInformation> ais = (List<AtmosphericInformation>) response.getEntity();
    assertEquals(Response.Status.OK, response.getStatusInfo());
    assertEquals(ais.get(0).getWind(), _dp);
  }
  
  @Test
  public void testWeatherGetInvalidIada() throws Exception {
    Response response = _query.weather("BBA", "0");
    assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
  }

}
