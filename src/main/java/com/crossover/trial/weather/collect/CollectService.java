package com.crossover.trial.weather.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crossover.trial.weather.exceptions.NotFoundWeatherException;
import com.crossover.trial.weather.exceptions.WeatherException;

import jersey.repackaged.com.google.common.collect.Maps;

/**
 * 
 * Service class to access and manipulate collect data
 * 
 * @author Peter Jerold Leslie
 *
 */
public class CollectService {
  private static final Logger LOGGER = Logger.getLogger(CollectService.class.getName());

  private final static ConcurrentMap<String, AirportData> airportDatas = Maps.newConcurrentMap();
  private final static ConcurrentMap<String, AtmosphericInformation> atmosphericInformationMap =
      Maps.newConcurrentMap();

  public Map<String, AirportData> getAirportDatas() {
    return airportDatas;
  }

  public ConcurrentMap<String, AtmosphericInformation> getAtmosphericInformationMap() {
    return atmosphericInformationMap;
  }

  /**
   * Add a new known airport to our list.
   *
   * @param iataCode 3 letter code
   * @param latitude in degrees
   * @param longitude in degrees
   *
   * @return the added airport
   * @throws Exception
   */
  public AirportData addAirport(String iataCode, String latitude, String longitude)
      throws Exception {
    AirportData ad;
    try {
      ad = new AirportData.Builder().withIata(iataCode).withLatitude(Double.valueOf(latitude))
          .withLongitude(Double.valueOf(longitude)).build();
      airportDatas.put(iataCode, ad);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, null, e);
      throw e;
    }
    return ad;
  }

  public AirportData addAirport(AirportData ad) throws Exception {
    try {
      airportDatas.put(ad.getIata(), ad);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, null, e);
      throw e;
    }
    return ad;
  }

  /**
   * Given an iataCode find the airport data
   *
   * @param iataCode as a string
   * @return airport data or null if not found
   */
  public AirportData findAirport(String iataCode) {
    return airportDatas.get(iataCode);
  }

  /**
   * Given an iataCode to delete the airport data
   *
   * @param iataCode as a string
   * @return airport data or null if not found
   */
  public AirportData deleteAirport(String iataCode) {
    atmosphericInformationMap.remove(iataCode);
    return airportDatas.remove(iataCode);
  }

  /**
   * Given an iataCode find the atmospheric information data
   *
   * @param iataCode as a string
   * @return atmospheric information data or null if not found
   */
  public AtmosphericInformation findAtmosphericInformation(String iataCode) {
    return atmosphericInformationMap.get(iataCode);
  }

  /**
   * This method is used to clear airport data and atmospheric information data
   */
  public void clean() {
    airportDatas.clear();
    atmosphericInformationMap.clear();
  }


  /**
   * Update the airports weather data with the collected data.
   *
   * @param iataCode the 3 letter IATA code
   * @param pointType the point type {@link DataPointType}
   * @param dp a datapoint object holding pointType data
   *
   * @throws WeatherException if the update can not be completed
   * @throws NotFoundWeatherException if airport not found
   */
  public void addDataPoint(String iataCode, String pointType, DataPoint dp)
      throws WeatherException, NotFoundWeatherException, IllegalArgumentException {
    if (airportDatas.get(iataCode) == null) {
      throw new NotFoundWeatherException();
    }
    atmosphericInformationMap.putIfAbsent(iataCode, new AtmosphericInformation());
    final AtmosphericInformation ai = atmosphericInformationMap.get(iataCode);
    updateAtmosphericInformation(ai, pointType, dp);
  }

  /**
   * update atmospheric information with the given data point for the given point type
   *
   * @param ai the atmospheric information object to update
   * @param pointType the data point type as a string
   * @param dp the actual data point
   */
  private void updateAtmosphericInformation(AtmosphericInformation ai, final String pointType,
      DataPoint dp) throws WeatherException, IllegalArgumentException {

    DataPointType dptype = null;
    try {
      dptype = DataPointType.valueOf(pointType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw e;
    }

    switch (dptype) {
      case WIND:
        if (dp.getMean() >= 0) {
          ai.setWind(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid wind Value");
        }
      case TEMPERATURE:
        if (dp.getMean() >= -50 && dp.getMean() < 100) {
          ai.setTemperature(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid temperature value");
        }
      case HUMIDTY:
        if (dp.getMean() >= 0 && dp.getMean() < 100) {
          ai.setHumidity(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid humidity value");
        }
      case PRESSURE:
        if (dp.getMean() >= 650 && dp.getMean() < 800) {
          ai.setPressure(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid pressure value");
        }
      case CLOUDCOVER:
        if (dp.getMean() >= 0 && dp.getMean() < 100) {
          ai.setCloudCover(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid cloudcover value");
        }
      case PRECIPITATION:
        if (dp.getMean() >= 0 && dp.getMean() < 100) {
          ai.setPrecipitation(dp);
          ai.setLastUpdateTime(System.currentTimeMillis());
          return;
        } else {
          throw new WeatherException("invalid precipitation value");
        }
      default:
        break;
    }
  }
}
