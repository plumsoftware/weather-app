
package ru.plumsoftware.weatherapp.weatherdata.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Alerts {

    @SerializedName("alert")
    @Expose
    private List<Alert> alert = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Alerts() {
    }

    /**
     * 
     * @param alert
     */
    public Alerts(List<Alert> alert) {
        super();
        this.alert = alert;
    }

    public List<Alert> getAlert() {
        return alert;
    }

    public void setAlert(List<Alert> alert) {
        this.alert = alert;
    }

}
