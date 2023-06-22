package ru.plumsoftware.weatherapp.links;

public abstract class Link {
    public static final String API_KEY_1 = "xxxx89dfe5734725a09155301221203"; //Forecast
    public static final String API_KEY_2 = "xxxxcdab0f80b414d35b6d4b0f3e752e"; //Current

    public static final String BASE_URL = "https://openweathermap.org/";
    public static final String BASE_API_URL = "https://api.openweathermap.org/";
    public static final String BASE_API_URL_FORECAST = "https://api.weatherapi.com/v1/";
    public static final String BASE_API_URL_MAP = "https://tile.openweathermap.org/";
    public static final String IMG_URL = "img/wn/";
    public static final String IMG_POSTFIX = "@2x";
    public static final String IMG_FORMAT = ".png";

    public static final String lINK_TO_IMAGE = "https://openweathermap.org/img/wn/10d@2x.png";

    public static class Builder{

        public static String build(String imgName){
            return BASE_URL +
                    IMG_URL +
                    imgName +
                    IMG_POSTFIX +
                    IMG_FORMAT;
        }
    }
}
