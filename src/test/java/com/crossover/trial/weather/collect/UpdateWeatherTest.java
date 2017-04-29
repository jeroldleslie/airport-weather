package com.crossover.trial.weather.collect;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.WeatherCollectorEndpoint;
import com.crossover.trial.weather.WeatherQueryEndpoint;
import com.crossover.trial.weather.query.RestWeatherQueryEndpoint;
import com.google.gson.Gson;

public class UpdateWeatherTest {
  private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();
  private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();
  private CollectService collectService = new CollectService();
  private Gson _gson = new Gson();

  private DataPoint _dp;

  @Before
  public void setUp() throws Exception {
    collectService.clean();
    collectService.addAirport("BOS", "42.364347", "-71.005181");
    collectService.addAirport("EWR", "40.6925", "-74.168667");
    collectService.addAirport("JFK", "40.639751", "-73.778925");
    collectService.addAirport("LGA", "40.777245", "-73.872608");
    collectService.addAirport("MMU", "40.79935", "-74.4148747");
  }

  @After
  public void tearDown() {
    collectService.clean();
    _query = null;
    _update = null;
    _dp = null;
  }
  
  @Test
  public void testValidQuery(){
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    Response response = _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
    assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
  }
  
  @Test
  public void testInvalidAirport(){
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    Response response = _update.updateWeather("BOSXXX", "wind", _gson.toJson(_dp));
    assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
  }
  
  @Test
  public void testInvalidDataPointType(){
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    Response response = _update.updateWeather("BOS", "windx", _gson.toJson(_dp));
    assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
  }
  
  @Test
  public void testInvalidDataPointValue(){
    _dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30)
        .withMean(22).build();
    Response response = _update.updateWeather("BOS", "pressure", _gson.toJson(_dp));
    assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
  }
}
