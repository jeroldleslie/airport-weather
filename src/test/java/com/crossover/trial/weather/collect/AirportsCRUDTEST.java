package com.crossover.trial.weather.collect;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.WeatherCollectorEndpoint;
import com.crossover.trial.weather.WeatherQueryEndpoint;
import com.crossover.trial.weather.query.RestWeatherQueryEndpoint;
import com.google.gson.Gson;

public class AirportsCRUDTEST {

  private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();
  private WeatherCollectorEndpoint _collect = new RestWeatherCollectorEndpoint();
  private CollectService collectService = new CollectService();
  private Gson _gson = new Gson();
  private DataPoint _dp;

  @Before
  public void setUp() throws Exception {
    collectService.clean();
  }

  @After
  public void tearDown() {
    collectService.clean();
    _query = null;
    _collect = null;
    _dp = null;
  }

  @Test
  public void testAddAirport() {
    Response response = _collect.addAirport("EWR", "40.6925", "-74.168667");
    assertEquals(Response.Status.OK, response.getStatusInfo());
    AirportData actual = _gson.fromJson(response.getEntity().toString(), AirportData.class);
    assertEquals("EWR", actual.getIata());
    assertEquals("40.6925", actual.getLatitude() + "");
    assertEquals("-74.168667", actual.getLongitude() + "");
  }

  @Test
  public void testInvalidLat() {
    Response response = _collect.addAirport("EWR", "xxx", "-74.168667");
    assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
  }

  @Test
  public void testInvalidLon() {
    Response response = _collect.addAirport("EWR", "40.6925", "xxx");
    assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
  }

  @Test
  public void testAddAirportInService() throws Exception {
    AirportData actual = collectService.addAirport("EWR", "40.6925", "-74.168667");
    assertEquals("EWR", actual.getIata());
    assertEquals("40.6925", actual.getLatitude() + "");
    assertEquals("-74.168667", actual.getLongitude() + "");
  }

  @Test(expected = NumberFormatException.class)
  public void testInvalidLatInService() throws Exception {
    collectService.addAirport("EWR", "xxx", "-74.168667");
  }

  @Test(expected = NumberFormatException.class)
  public void testInvalidLonInService() throws Exception {
    collectService.addAirport("EWR", "40.6925", "xxx");
  }

  @Test
  public void testGetAirports() {
    _collect.addAirport("BOS", "42.364347", "-71.005181");
    _collect.addAirport("EWR", "40.6925", "-74.168667");
    _collect.addAirport("JFK", "40.639751", "-73.778925");
    _collect.addAirport("LGA", "40.777245", "-73.872608");
    _collect.addAirport("MMU", "40.79935", "-74.4148747");

    Response response = _collect.getAirports();
    Set<String> retval = (Set<String>) response.getEntity();
    assertEquals(Response.Status.OK, response.getStatusInfo());
    assertEquals(5, retval.size());
  }
  
  @Test
  public void testClean() {
    _collect.addAirport("BOS", "42.364347", "-71.005181");
    _collect.addAirport("EWR", "40.6925", "-74.168667");
    _collect.addAirport("JFK", "40.639751", "-73.778925");
    _collect.addAirport("LGA", "40.777245", "-73.872608");
    _collect.addAirport("MMU", "40.79935", "-74.4148747");

    collectService.clean();
    Response response = _collect.getAirports();
    Set<String> retval = (Set<String>) response.getEntity();
    assertEquals(Response.Status.OK, response.getStatusInfo());
    assertEquals(0, retval.size());
  }
  
  
  @Test
  public void testGetAirport() {
    _collect.addAirport("LGA", "40.777245", "-73.872608");

    Response response = _collect.getAirport("LGA");
    AirportData ad = (AirportData) response.getEntity();
    assertEquals(Response.Status.OK, response.getStatusInfo());
    assertEquals("LGA", ad.getIata());
  }
  
  @Test
  public void testGetAirportNotFound() {
    _collect.addAirport("LGA", "40.777245", "-73.872608");
    Response response = _collect.getAirport("BBA");
    assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
  }
  
  @Test
  public void testDeleteAirport() {
    _collect.addAirport("LGA", "40.777245", "-73.872608");

    Response response = _collect.getAirport("LGA");
    assertEquals(Response.Status.OK, response.getStatusInfo());
    
    response = _collect.deleteAirport("LGA");
    assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
    
    response = _collect.getAirport("LGA");
    assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
  }
  
  @Test
  public void testDeleteAirportNotFound() {
    _collect.addAirport("LGA", "40.777245", "-73.872608");
    Response response = _collect.getAirport("BBA");
    assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
  }
  

}
