package edu.uw.tcss450.team4projectclient.ui.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uw.tcss450.team4projectclient.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.team4projectclient.utils.PasswordValidator;

import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdLength;

/**
 * Weather page
 */
public class WeatherFragment extends Fragment {
    // static var for the zipcode
    public static String zipcode = "98402";
    // Binding to have access to all of the components in the xml.
    private FragmentWeatherBinding binding;
    // This is the view model class to help with signing in
    private WeatherViewModel mWeatherModel;
    //checks the zipcode entered is of length 5
    private PasswordValidator mZipcodeValidator = PasswordValidator.checkLength(5)
            .and(checkExcludeWhiteSpace())
            .and(PasswordValidator.checkOnlyDigits());
    /**
     * Required empty public constructor
     */
    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Sets up the view for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }
    /**
     * onCreate that gets called when the class is initialized
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting the view model
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
    }

    /**
     * Gets called when the view gets created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setting click listener to the search button
        binding.buttonSearchWeather.setOnClickListener(button -> validateZipcode());
        //adding observing
        mWeatherModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );
//        Log.e("init binding", "bind " +binding.enterZipcode.getText().toString());
        pullWeatherData();
    }

    /**
     * validate User's input for a valid zipcodes.
     */
    private void validateZipcode() {
        mZipcodeValidator.processResult(
                mZipcodeValidator.apply(binding.enterZipcode.getText().toString()),
                this::pullWeatherData,this::passwordError);
    }
    /**
     * Sets user error.
     *
     * @param validationResult
     */
    private void passwordError(PasswordValidator.ValidationResult validationResult) {
        binding.layoutWait.setVisibility(View.GONE);
        String errorType;
        // selects exact failure in entered zipcode
        switch (validationResult) {
            case PWD_NOT_NUMERIC: errorType = "Zipcode may only contain numeric values"; break;
            case PWD_INVALID_LENGTH: errorType = "Zipcode must be 5 digits long"; break;
            default: errorType = "Please enter a valid zipcode";
        }
        binding.enterZipcode.setError(errorType);
    }

    /**
     * Sends all of the required information to the server to register the user.
     */
    private void pullWeatherData() {

        binding.layoutWait.setVisibility(View.VISIBLE);
        if (binding.enterZipcode.getText().toString().isEmpty()) {
            mWeatherModel.connect(zipcode);
        } else {
            mWeatherModel.connect(binding.enterZipcode.getText().toString());
        }


    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        Log.e("Response", String.valueOf(response));

        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    // sets backend error response
                    binding.enterZipcode.setError(new JSONObject(response.getString("data")
                                         .replace("'", "\"")).getString("message"));

                    binding.layoutWait.setVisibility(View.GONE);
                } catch (JSONException e) {

//                    Log.e("test", response.toString());
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    //gets servers response
                  JSONObject json =  new JSONObject(String.valueOf(response));
                  //set city and state
                  String city = json.getString("city");
                  String state = json.getString("state");
                  binding.textCityState.setText(city + ", " + state);
                  // set date
                  DateFormat format = new SimpleDateFormat(" d, y");
                  binding.textDate.setText(Calendar.getInstance()
                                                   .getDisplayName(Calendar
                                                   .MONTH, Calendar.LONG, Locale.getDefault())
                                                   + format.format(new Date()));
                  //get open weather maps json response
                  String weather = json.getString("weather").replace('\\',' ');
                  //call class that will add all information to fragment
                  new Weather(new JSONObject(weather), binding);
                  if (!binding.enterZipcode.getText().toString().isEmpty()) {
                      zipcode = binding.enterZipcode.getText().toString();
                  }
                  binding.layoutWait.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }


}

