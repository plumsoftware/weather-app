package ru.plumsoftware.weatherapp.items;

public class AirQualityData {
    private String name;
    private int color;
    private float value;

    public AirQualityData(String name, int color, float value) {
        this.name = name;
        this.color = color;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
