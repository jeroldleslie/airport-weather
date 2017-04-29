package com.crossover.trial.weather.collect;

import static com.crossover.trial.weather.util.Utils.fromJson;
import static com.crossover.trial.weather.util.Utils.toJson;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.WeatherCollectorEndpoint;
import com.crossover.trial.weather.exceptions.NotFoundWeatherException;
import com.crossover.trial.weather.exceptions.WeatherException;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
  private static final Logger LOGGER =
      Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

  /**
   * the collect service
   */
  private final CollectService collectService = new CollectService();

  public CollectService getAwsService() {
    return collectService;
  }

  /* (non-Javadoc)
   * @see com.crossover.trial.weather.WeatherCollectorEndpoint#ping()
   */
  @Override
  public Response ping() {
    return Response.status(Response.Status.OK).entity("ready").build();
  }

  /* (non-Javadoc)
   * @see com.crossover.trial.weather.WeatherCollectorEndpoint#updateWeather(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Response updateWeather(String iataCode, String pointType, String datapointJson) {
    try {
      collectService.addDataPoint(iataCode, pointType, fromJson(datapointJson, DataPoint.class));
    } catch (Exception e) {
      if (e instanceof WeatherException) {
        LOGGER.log(Level.WARNING, null, e);
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
      } else if (e instanceof NotFoundWeatherException) {
        LOGGER.log(Level.WARNING, null, e);
        return Response.status(Response.Status.NOT_FOUND).build();
      } else if (e instanceof IllegalArgumentException) {
        LOGGER.log(Level.WARNING, null, e);
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid datapoint type")
            .build();
      } else {
        LOGGER.log(Level.SEVERE, null, e);
        Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      }
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  /* (non-Javadoc)
   * @see com.crossover.trial.weather.WeatherCollectorEndpoint#getAirports()
   */
  @Override
  public Response getAirports() {
    Set<String> retval = collectService.getAirportDatas().keySet();
    return Response.status(Response.Status.OK).entity(retval).build();
  }

  /* (non-Javadoc)
   * @see com.crossover.trial.weather.WeatherCollectorEndpoint#getAirport(java.lang.String)
   */
  @Override
  public Response getAirport(String iata) {
    AirportData ad = collectService.findAirport(iata);
    if (ad == null) {
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Airport with the iata " + iata + " is not found").build();
    }
    return Response.status(Response.Status.OK).entity(ad).build();
  }

  @Override
  public Response addAirport(String iata, String latString, String longString) {
    try {
      AirportData ad = collectService.addAirport(iata, latString, longString);
      return Response.status(Response.Status.OK).entity(toJson(ad)).build();
    } catch (Exception e) {
      if (e instanceof NumberFormatException) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Latitude or Longitude is not valid").build();
      }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public Response deleteAirport(String iata) {
    AirportData airportData = collectService.deleteAirport(iata);
    if (airportData != null) {
      return Response.status(Response.Status.NO_CONTENT).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Airport with the iata " + iata + " is not found").build();
    }
  }

  @Override
  public Response exit() {
    System.exit(0);
    return Response.noContent().build();
  }

  @Override
  public Response addAirport(String data) {
    AirportData value = fromJson(data, AirportData.class);
    try {
      AirportData ad = collectService.addAirport(value);
      return Response.status(Response.Status.OK).entity(toJson(ad)).build();
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.log(Level.SEVERE, null, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }


}
