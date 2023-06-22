package ru.plumsoftware.weatherapp.items;

public class Data {
    private String name, value;
    private int resId;

    public Data(String name, String value, int resId) {
        this.name = name;
        this.value = value;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
