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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentFavoriteBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    // This is the view model class for the favorites
    private static FavoriteViewModel mFavoriteModel;
    // Binding to have access to all of the components in the xml
    private FragmentFavoriteBinding binding;
    /**
     * The ViewModel containing the user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mFavoriteModel = new ViewModelProvider(getActivity()).get(FavoriteViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return this.binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //adding observing
        mFavoriteModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );
        mFavoriteModel.connect(mUserModel.getId());


    }

    public static List<FavoriteData> getLocations(JSONArray locations) throws JSONException {
//        Log.e("hmmmmm", "STRING LAT: " + locations.toString());
        List<FavoriteData> data = new ArrayList<FavoriteData>();
        for (int i = 0; i < locations.length(); i++) {
            JSONObject item = locations.getJSONObject(i);
//
            data.add(new FavoriteData(item.getString("lat"), item.getString("long"),
                    item.getString("zip"), item.getString("city"),item.getString("state")));

        }
//        Log.e("DATA", data.toString());
        return data;
    }

    public static void deleteLocation(int memberid, String zipcode) {
        mFavoriteModel.connect(memberid, zipcode);
    }



    /**
     * An observer on the HTTP Response from the web server.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        Log.e("Response", String.valueOf(response));

        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    // sets backend error response
                    String message = new JSONObject(response.getString("data")
                            .replace("'", "\"")).getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

//                    binding.layoutWait.setVisibility(View.GONE);
                } catch (JSONException e) {

//                    Log.e("test", response.toString());
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {

                try {
//                    //gets servers response
                    JSONObject json =  new JSONObject(String.valueOf(response));

                    String type = json.getString("type");
                    Log.e("Type", type);
                    if (type.equals("delete")) {
                        mFavoriteModel.connect(mUserModel.getId());
                        Toast.makeText(getContext(), "Location deleted", Toast.LENGTH_LONG).show();
                    } else if (type.equals("favorites")) {
                        List<FavoriteData> favs = getLocations(new JSONArray(json.getString("locations")));
//                        Log.e("DATA", favs.toString());
                        binding.RVFavorites.setAdapter(new FavoriteViewAdapter(favs, mUserModel.getId()));

                    }
//                    binding.layoutWait.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

}
