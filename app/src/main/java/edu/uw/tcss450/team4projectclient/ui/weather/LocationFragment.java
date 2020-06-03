package edu.uw.tcss450.team4projectclient.ui.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.uw.tcss450.team4projectclient.MainActivity;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentLocationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment  implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private LocationViewModel mModel;
    private GoogleMap mMap;
    FragmentLocationBinding binding;


    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity())
                .setActionBarTitle("Google Maps");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("ooooo", "lol222");
        binding = FragmentLocationBinding.bind(getView());
        mModel = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        Log.e("ooooo", "lol");
        mModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            Log.e("yayyyy", location.toString());
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            binding.textLatitude.setText("Latitude: " + lat);
            binding.textLongitude.setText("Longitude: " + lon);
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);

        binding.buttonSearchWeather.setOnClickListener(button -> sendWeatherData());
    }

    private void sendWeatherData() {
        String lat = binding.textLatitude.getText().toString();
        String lon = binding.textLongitude.getText().toString();
        WeatherFragment.sendCoords(lat.substring(10), lon.substring(11));
        Navigation.findNavController(getView()).navigate(LocationFragmentDirections
                                               .actionGoogleFragmentToNavigationWeather());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);
                final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
                //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
            }
        });
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.e("LAT/LONG", latLng.toString());

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
//        binding.textLatLong.setText(latLng.toString());
        String lat = String.valueOf(latLng.latitude);
        String lon = String.valueOf(latLng.longitude);
        binding.textLatitude.setText("Latitude: " + lat.substring(0,10));
        binding.textLongitude.setText("Longitude: " + lon.substring(0,10));

    }
}
