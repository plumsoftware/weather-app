
package ru.plumsoftware.weatherapp.weatherdata.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ForecastWeather {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("forecast")
    @Expose
    private Forecast forecast;
    @SerializedName("alerts")
    @Expose
    private Alerts alerts;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ForecastWeather() {
    }

    /**
     * 
     * @param alerts
     * @param current
     * @param location
     * @param forecast
     */
    public ForecastWeather(Location location, Current current, Forecast forecast, Alerts alerts) {
        super();
        this.location = location;
        this.current = current;
        this.forecast = forecast;
        this.alerts = alerts;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Alerts getAlerts() {
        return alerts;
    }

    public void setAlerts(Alerts alerts) {
        this.alerts = alerts;
    }

}
