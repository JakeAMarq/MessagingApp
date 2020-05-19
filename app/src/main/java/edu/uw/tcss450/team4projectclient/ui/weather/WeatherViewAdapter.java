package edu.uw.tcss450.team4projectclient.ui.weather;

import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;

import edu.uw.tcss450.team4projectclient.databinding.FragmentWeatherCardBinding;
/**
 * Adpater for the recycle view
 */
public class WeatherViewAdapter extends RecyclerView.Adapter<WeatherViewAdapter.WeatherViewHolder> {
    //List of type WeatherData for each card
    public  List<WeatherData> weatherData;
    /**
     * required constructor
     * @param weather
     */
    public WeatherViewAdapter ( List<WeatherData> weather) {
        weatherData = weather;
    }
    /**
     * Gets called when each card gets created.
     * @param parent
     * @param viewType
     */
    @NonNull
    @Override
    public WeatherViewAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the holder to fragment_weather_card
        return new WeatherViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_weather_card, parent, false));
    }
    /**
     *calls and sets all data for each card at given position
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull WeatherViewAdapter.WeatherViewHolder holder, int position) {
        holder.setWeatherData(weatherData.get(position));
    }
    /**
     * returns size of the card set
     * @return size
     */
    @Override
    public int getItemCount() {
        return weatherData.size();
}

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        // instance of the view
        public final View mView;
        // instance of the weathercardbing xml
        public FragmentWeatherCardBinding binding;
        /**
         * contructor to set up fields
         * @param view
         */
        public WeatherViewHolder(View view) {
            super(view);
            mView = view;
            //sets the binding
            binding = FragmentWeatherCardBinding.bind(view);

        }
        /**
         * Sets all the card items with data
         * @param WD
         */
        public void setWeatherData(WeatherData WD) {
            //set all textviews
            binding.textTime.setText(WD.getTime());
            binding.textTemp.setText(WD.getTemp());
            binding.textHumidity.setText(WD.getHumidity() + " %");
            binding.textWind.setText(WD.getWind() + " m/h");
            //set icon image using a static method in WeatherData
            binding.imageIcon.setImageIcon(Icon.createWithResource(
                        mView.getContext(), WeatherData.getImageViewIcon(WD.getIcon())));


        }

    }
}
