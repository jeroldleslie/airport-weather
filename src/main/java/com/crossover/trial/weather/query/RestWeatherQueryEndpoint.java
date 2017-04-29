package com.crossover.trial.weather.query;

import static com.crossover.trial.weather.util.Utils.toJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.WeatherQueryEndpoint;
import com.crossover.trial.weather.collect.AtmosphericInformation;
import com.crossover.trial.weather.exceptions.NotFoundWeatherException;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently,
 * all data is held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

  private static final Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

  private final QueryService service = new QueryService();

  /**
   * Retrieve service health including total size of valid data points and request frequency
   * information.
   *
   * @return health stats for the service as a string
   */
  @Override
  public String ping() {
    final Map<String, Object> retval = new HashMap<>();
    retval.put("datasize", service.getDatasize());
    retval.put("iata_freq", service.getIataFreq());
    retval.put("radius_freq", service.getRadiusFreq());
    try {
      return toJson(retval);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, null, e);
      return "Not able to parse result";
    }
  }

  /**
   * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport
   * information and return a list of matching atmosphere information.
   *
   * @param iata the iataCode
   * @param radiusString the radius in km
   *
   * @return a list of atmospheric information
   */
  @Override
  public Response weather(String iata, String radiusString) {
    try {
      double radius =
          radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
      List<AtmosphericInformation> retval = service.getWeather(iata, radius);
      return Response.status(Response.Status.OK).entity(retval).build();
    } catch (Exception e) {
      if (e instanceof NumberFormatException) {
        LOGGER.log(Level.WARNING, null, e);
        return Response.status(Response.Status.BAD_REQUEST).entity("invalid radius").build();
      } else if (e instanceof NotFoundWeatherException) {
        LOGGER.log(Level.WARNING, null, e);
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      LOGGER.log(Level.SEVERE, null, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
