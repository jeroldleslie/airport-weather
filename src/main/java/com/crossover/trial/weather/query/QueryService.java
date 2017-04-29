package com.crossover.trial.weather.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crossover.trial.weather.collect.AirportData;
import com.crossover.trial.weather.collect.AtmosphericInformation;
import com.crossover.trial.weather.collect.CollectService;
import com.crossover.trial.weather.exceptions.NotFoundWeatherException;

import jersey.repackaged.com.google.common.collect.Maps;

/**
 * Service class to access and manipulate query data
 * 
 * @author Peter Jerold Leslie
 *
 */
public class QueryService {
  
  /**
   * Internal performance counter to better understand most requested information, this map can be
   * improved but for now provides the basis for future performance optimizations. Due to the
   * stateless deployment architecture we don't want to write this to disk, but will pull it off
   * using a REST request and aggregate with other performance metrics {@link #ping()}
   */
  protected final static Map<AirportData, Integer> requestFrequency = Maps.newConcurrentMap();
  protected final static Map<Double, Integer> radiusFreq = Maps.newConcurrentMap();

  /** collect service to access data */
  private final CollectService collectService = new CollectService();

  /** earth radius in KM */
  private final static double R = 6372.8;

  /**
   * @param ad the airport data
   * @param radius query radius
   */
  protected void updateRequestFrequency(AirportData ad, double radius) {
    requestFrequency.put(ad, requestFrequency.getOrDefault(ad, 0) + 1);
    radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0) + 1);
  }


  /**
   * @return datasize of the last updated atmospheric information
   */
  protected int getDatasize() {
    int datasize = 0;
    for (Map.Entry<String, AtmosphericInformation> e : collectService.getAtmosphericInformationMap()
        .entrySet()) {
      if (e.getValue().getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
        datasize++;
      }
    }
    return datasize;
  }

  /**
   * @return airport data request frequency
   */
  protected Map<String, Double> getIataFreq() {
    final Map<String, Double> freq = new HashMap<>();
    for (Map.Entry<String, AirportData> entry : collectService.getAirportDatas().entrySet()) {
      AirportData ad = entry.getValue();
      int rfSize = requestFrequency.size();
      if (rfSize != 0) {
        double frac = (double) requestFrequency.getOrDefault(ad, 0) / requestFrequency.size();
        freq.put(ad.getIata(), frac);
      }
    }
    return freq;
  }

  /**
   * @return requested radius frequency 
   */
  protected int[] getRadiusFreq() {
    int m = radiusFreq.keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;
    int[] hist = new int[m];
    for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
      int i = e.getKey().intValue() % 10;
      hist[i] += e.getValue();
    }
    return hist;
  }

  /**
   * @param iata the iata code of airport
   * @param radius the geo location distance radius in km 
   * @return list of available atmospheric information
   * @throws NotFoundWeatherException
   */
  protected List<AtmosphericInformation> getWeather(String iata, double radius)
      throws NotFoundWeatherException {
    final AirportData ad = collectService.findAirport(iata);
    if (ad == null) {
      throw new NotFoundWeatherException();
    }
    updateRequestFrequency(ad, radius);
    final List<AtmosphericInformation> result = new ArrayList<>();
    if (radius == 0) {
      addWeatherResult(result, iata);
    } else {
      collectService.getAirportDatas().forEach((k, v) -> {
        if (calculateDistance(ad, v) <= radius) {
          addWeatherResult(result, v.getIata());
        }
      });
    }
    return result;
  }


  /**
   * @param aIList the Atmospheric Information list
   * @param iata the iata code of airport
   */
  private void addWeatherResult(List<AtmosphericInformation> aIList, String iata) {
    AtmosphericInformation ai = collectService.findAtmosphericInformation(iata);
    if (ai != null) {
      aIList.add(ai);
    }
  }

  /**
   * Haversine distance between two airports.
   *
   * @param ad1 airport 1
   * @param ad2 airport 2
   * @return the distance in KM
   */
  private static double calculateDistance(AirportData ad1, AirportData ad2) {
    double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
    double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
    double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
        * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
    double c = 2 * Math.asin(Math.sqrt(a));
    return R * c;
  }
}
