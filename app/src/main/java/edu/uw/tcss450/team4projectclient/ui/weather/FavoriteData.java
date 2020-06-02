package edu.uw.tcss450.team4projectclient.ui.weather;

public class FavoriteData {
    // the Latitude of weather displayed
    private String mLatitude;
    // the Longitude of weather displayed
    private String mLongitude;
    // the zipcode of weather displayed
    private String mZipcode;
    // the city of weather displayed
    private String mCity;
    // the state speed of weather displayed
    private String mState;
    // identification for the database value

    /**
     * Constructor for the WeatherData
     * @param lat
     * @param lon
     * @param zip
     * @param city
     * @param state
     */
    public FavoriteData(String lat, String lon, String zip, String city, String state){
        mLatitude = lat;
        mLongitude = lon;
        mZipcode = zip;
        mCity = city;
        mState = state;
    }
    /**
     * returns city
     */
    public String getCity() { return mCity; }
    /**
     * returns state
     */
    public String getState() { return mState; }
    /**
     * returns lat
     */
    public String getLat () { return mLatitude;}
    /**
     * returns long
     */
    public String getLong () { return mLongitude;}
    /**
     * returns zipcode
     */
    public String getZipcode () { return mZipcode;}

}
