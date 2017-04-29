package com.crossover.trial.weather.collect;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {
  // /** id of AirportData */
  // private int id;

  /** the name of airport */
  private String name;

  /** the three letter IATA code */
  private String iata;

  /** latitude value in degrees */
  private double latitude;

  /** longitude value in degrees */
  private double longitude;

  /** city of airport */
  private String city;

  /** country or territory where airport is located */
  private String country;

  /** 4-letter ICAO code */
  private String icao;

  /** the altitude accuracy of position in feet */
  private double altitude;

  /** hours offset from UTC. Fractional hours are expressed as decimals. */
  private double timezone;

  /**
   * one of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None)
   * or U (Unknown)
   */
  private String dst;

  /**
   * @param name
   * @param iata
   * @param latitude
   * @param longitude
   * @param city
   * @param country
   * @param icao
   * @param altitude
   * @param timezone
   * @param dst
   */
  protected AirportData(String name, String iata, double latitude, double longitude, String city,
      String country, String icao, double altitude, double timezone, String dst) {
    super();
    this.name = name;
    this.iata = iata;
    this.latitude = latitude;
    this.longitude = longitude;
    this.city = city;
    this.country = country;
    this.icao = icao;
    this.altitude = altitude;
    this.timezone = timezone;
    this.dst = dst;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIata() {
    return iata;
  }

  public void setIata(String iata) {
    this.iata = iata;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getIcao() {
    return icao;
  }

  public void setIcao(String icao) {
    this.icao = icao;
  }

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  public double getTimezone() {
    return timezone;
  }

  public void setTimezone(double timezone) {
    this.timezone = timezone;
  }

  public String getDst() {
    return dst;
  }

  public void setDst(String dst) {
    this.dst = dst;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof AirportData) {
      return ((AirportData) other).getIata().equals(this.getIata());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.iata.hashCode();
  }

  static public class Builder {
    String name;
    String iata;
    double latitude;
    double longitude;
    String city;
    String country;
    String icao;
    double altitude;
    double timezone;
    String dst;

    public Builder() {}

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withIata(String iata) {
      this.iata = iata;
      return this;
    }

    public Builder withLatitude(double latitude) {
      this.latitude = latitude;
      return this;
    }

    public Builder withLongitude(double longitude) {
      this.longitude = longitude;
      return this;
    }

    public Builder withCity(String city) {
      this.city = city;
      return this;
    }

    public Builder withCountry(String country) {
      this.country = country;
      return this;
    }

    public Builder withIcao(String icao) {
      this.icao = icao;
      return this;
    }

    public Builder withAltitude(double altitude) {
      this.altitude = altitude;
      return this;
    }

    public Builder withTimezone(double timezone) {
      this.timezone = timezone;
      return this;
    }

    public Builder withDst(String dst) {
      this.dst = dst;
      return this;
    }

    public AirportData build() {
      return new AirportData(name, iata, latitude, longitude, city, country, icao, altitude, timezone, dst);
    }
  }
}
