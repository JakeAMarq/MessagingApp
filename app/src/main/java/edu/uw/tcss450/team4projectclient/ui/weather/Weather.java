package edu.uw.tcss450.team4projectclient.ui.weather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.uw.tcss450.team4projectclient.databinding.FragmentWeatherBinding;
/**
 * Disects all Weather data from json object
 */
public class Weather {
    // reference to the binding
    private FragmentWeatherBinding binding;
    //wather Jsonobject from the server
    private JSONObject weatherOBJ;
    // list of hourly weather for the recylcerview
    private List<WeatherData> hourlyDataList;
    // list of daily weather for the recylcerview
    private List<WeatherData> dailyDataList;
    //current day of week, sunday starting at 1
    private int weekDay;
    // list of the days of the week
    private String[] days;

    /**
     *  Constructer to set fields and call methods
     * @param weather
     * @param bind
     */
    public Weather(JSONObject weather, FragmentWeatherBinding bind) throws JSONException {
        //instantiates all fields
        binding =  bind;
        weatherOBJ = weather;
        hourlyDataList = new ArrayList<>();
        dailyDataList = new ArrayList<>();
        //fills list of week days
        days = new String[]{"Sun", "Mon","Tues", "Wed", "Thur" , "Fri", "Sat"};
        //get day of week
        Calendar cal = Calendar.getInstance();
        weekDay = cal.get(Calendar.DAY_OF_WEEK);
        // calls all current, hourly, and daily weather
        currentWeather();
        dailyWeather();
        hourlyWeather();
        //connects recycler view to the adapter to create the cards
        binding.dailyRv.setAdapter(new WeatherViewAdapter(dailyDataList));
        binding.hourlyRV.setAdapter(new WeatherViewAdapter(hourlyDataList));
    }
    /**
     * Sets all Json data to the current data
     */
    private void currentWeather() throws JSONException {
        JSONObject current = weatherOBJ.getJSONObject("current");
        binding.textTemp.setText(current.get("temp").toString().substring(0, 4) + '\u00B0');
        binding.textFeelsLike.setText("feels like " + current.get("feels_like").toString().substring(0, 4) + '\u00B0');
        binding.textWindSpeed.setText("wind speed: " + current.get("wind_speed").toString() + " m/h");
        binding.textHumidity.setText("humidity: " + current.get("humidity").toString() + " %");
        binding.textClouds.setText("clouds: " + current.get("clouds").toString() + " %");
        JSONObject weather = current.getJSONArray("weather").getJSONObject(0);
        binding.textDescription.setText(weather.get("description").toString());
        String icon = weather.get("icon").toString();
        binding.imageWeatherIcon.setImageResource(WeatherData.getImageViewIcon(icon));

    }
// Map of strings : List<Strings>
    /**
     * Sets all Json data to the daily data
     */
    private void dailyWeather() throws JSONException {
        //temp day-night, humidity, wind speed, icon
        JSONArray daily = weatherOBJ.getJSONArray("daily");
        // the OWM provides data for 8 days
        for (int i = 0; i < 8; i++) {
            JSONObject day = daily.getJSONObject(i);
            String time = chooseDay(i);
            String icon = day.getJSONArray("weather").getJSONObject(0).get("icon").toString();
            StringBuilder temp = new StringBuilder();
            //get temp for night and day
            temp.append(day.getJSONObject("temp").get("day").toString().substring(0, 2));
            temp.append('\u00B0' +"  |  ");
            temp.append(day.getJSONObject("temp").get("night").toString().substring(0, 2));
            temp.append('\u00B0');
            String humidity = day.get("humidity").toString();
            String wind = day.get("wind_speed").toString();
            // add WeatherData onject to the list
            dailyDataList.add(new WeatherData(time, icon, temp.toString(), humidity, wind));
        }

    }

//    {"Sun", "Mon","Tues", "Wed", "Thur" , "Fri", "Sat"}
    /**
     * Choose the correct label given the date and how many after today
     * @param daysLater
     */
    private String chooseDay(int daysLater) {
        int day = weekDay + daysLater;// sunday is 1
        //calculate correct day of week
        return  (day > 7) ? days[day - 8] : days[day - 1];
    }
    /**
     * Sets all Json data to the hourly data
     */
    private void hourlyWeather() throws JSONException {
        //temp, humidty, wind_speed, icon
        JSONArray hourly = weatherOBJ.getJSONArray("hourly");
        //get the time in 24hr format (0-23) to match OWM
        Date date = new Date();
        DateFormat formatHr = new SimpleDateFormat("H");
        int hr = Integer.parseInt(formatHr.format(date));
        // format we want displayed on card
        DateFormat formatTime = new SimpleDateFormat("h a");
        // display 25hours from current hour plus 24 hours more
        for (int i = 0; i < 25; i++) {
            JSONObject hour = hourly.getJSONObject(i);
            //calclate correct hour
            String curHour = (i > 24) ? Integer.toString(i - 24 + hr) : Integer.toString(i + hr);

            Date curDate = null;
            try {
                // parse the hour calculated above
                curDate = formatHr.parse(curHour);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("hourlyWeather","Parse Error");
            }
            // format the time
            String time = formatTime.format(curDate);
            //set all data
            String icon = hour.getJSONArray("weather").getJSONObject(0).get("icon").toString();
            String temp =   hour.get("temp").toString() + '\u00B0';
            String humidity = hour.get("humidity").toString();
            String wind = hour.get("wind_speed").toString();
            // add WeatherData onject to the list
            hourlyDataList.add(new WeatherData(time, icon, temp, humidity, wind));
        }
    }

}
