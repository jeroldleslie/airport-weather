package com.crossover.trial.weather.util;

import com.google.gson.Gson;

/**
 * Utils class to hold all common functionality as static methods
 * 
 * @author Peter Jerold Leslie
 *
 */
public class Utils {

  /** shared gson json to object factory */
  private final static Gson gson = new Gson();

  /**
   * @param object to convert to string
   * @return the json sting 
   */
  public static <T> String toJson(T val) {
    return gson.toJson(val);
  }

  /**
   * @param json then json string 
   * @param clazz the class name of the object to convert
   * @return the converted  object <T>
   */
  public static <T> T fromJson(String json, Class<T> clazz) {
    return gson.fromJson(json, clazz);
  }

}
