package edu.uw.tcss450.team4projectclient.ui.weather;


import edu.uw.tcss450.team4projectclient.R;
/**
 * Class to store all information for each card
 */
public class WeatherData {
    // the Time of weather displayed
    private String mTime;
    // the icons of weather displayed
    private String mIcon;
    // the Temp of weather displayed
    private String mTemp;
    // the humidity of weather displayed
    private String mHumidity;
    // the wind speed of weather displayed
    private String mWind;
    /**
     * Constructor for the WeatherData
     * @param time
     * @param icon
     * @param temp
     * @param humidity
     * @param wind
     */
    public WeatherData(String time, String icon, String temp, String humidity, String wind){
        mHumidity = humidity;
        mTime = time;
        mIcon = icon;
        mTemp = temp;
        mWind = wind;
    }
    /**
     * returns temp
     */
    public String getTemp() {
        return mTemp;
    }
    /**
     * returns time
     */
    public String getTime() {
        return mTime;
    }
    /**
     * returns icon
     */
    public String getIcon () { return mIcon;}

    /**
     * static method to retrieve the right resource given the string
     * @param icon
     */
    public static int getImageViewIcon(String icon) {

        int i;
        switch (icon) {
            case "01d": i = R.drawable.w01d; break;
            case "01n": i = R.drawable.w01n; break;
            case "02d": i = R.drawable.w02d; break;
            case "02n": i = R.drawable.w02n; break;
            case "03d": i = R.drawable.w03d; break;
            case "03n": i = R.drawable.w03n; break;
            case "04d": i = R.drawable.w04d; break;
            case "04n": i = R.drawable.w04n; break;
            case "09d": i = R.drawable.w09d; break;
            case "09n": i = R.drawable.w09n; break;
            case "10d": i = R.drawable.w10d; break;
            case "10n": i = R.drawable.w10n; break;
            case "11d": i = R.drawable.w11d; break;
            case "11n": i = R.drawable.w11n; break;
            case "13d": i = R.drawable.w13d; break;
            case "13n": i = R.drawable.w13n; break;
            case "50d": i = R.drawable.w50d; break;
            case "50n": i = R.drawable.w50n; break;
            default: i = R.drawable.ic_weather_black_24dp;
        }
        return i;
    }
    /**
     * returns humidity
     */
    public String getHumidity() {
        return mHumidity;
    }
    /**
     * returns wind
     */
    public String getWind() {
        return mWind;
    }

}
