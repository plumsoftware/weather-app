
package ru.plumsoftware.weatherapp.weatherdata.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Alert {

    @SerializedName("headline")
    @Expose
    private String headline;
    @SerializedName("msgtype")
    @Expose
    private String msgtype;
    @SerializedName("severity")
    @Expose
    private String severity;
    @SerializedName("urgency")
    @Expose
    private String urgency;
    @SerializedName("areas")
    @Expose
    private String areas;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("certainty")
    @Expose
    private String certainty;
    @SerializedName("event")
    @Expose
    private String event;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("effective")
    @Expose
    private String effective;
    @SerializedName("expires")
    @Expose
    private String expires;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("instruction")
    @Expose
    private String instruction;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Alert() {
    }

    /**
     * 
     * @param severity
     * @param note
     * @param expires
     * @param certainty
     * @param areas
     * @param effective
     * @param urgency
     * @param instruction
     * @param category
     * @param event
     * @param headline
     * @param msgtype
     * @param desc
     */
    public Alert(String headline, String msgtype, String severity, String urgency, String areas, String category, String certainty, String event, String note, String effective, String expires, String desc, String instruction) {
        super();
        this.headline = headline;
        this.msgtype = msgtype;
        this.severity = severity;
        this.urgency = urgency;
        this.areas = areas;
        this.category = category;
        this.certainty = certainty;
        this.event = event;
        this.note = note;
        this.effective = effective;
        this.expires = expires;
        this.desc = desc;
        this.instruction = instruction;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCertainty() {
        return certainty;
    }

    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEffective() {
        return effective;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

}
