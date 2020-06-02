package edu.uw.tcss450.team4projectclient.ui.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

import edu.uw.tcss450.team4projectclient.R;

public class FavoriteViewModel  extends AndroidViewModel {
    // keeps track of all fo the JSONObject responses.
    private MutableLiveData<JSONObject> mResponse;
    /**
     * constructor that initializes a mutable live data object.
     * @param application
     */
    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }
    /**
     * adding an observer for the login class.
     * @param owner
     * @param observer
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }
    /**
     * This method gets called in case an error happens during the existing user login process.
     * @param error
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:\"" + data +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * This method retrieves all of the members favorite location
     * @param memberid
     */
    public void connect(final int memberid) {
        //favorite locations endpoint
        String url = "https://team4-tcss450-project-server.herokuapp.com/favorites?memberid=" + memberid;
        // sends a get request to the link String url
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                mResponse::setValue,
                this::handleError) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
    /**
     * This methods deletes a location.
     * @param memberid
     * @param zipcode
     */
    public void connect(final int memberid, String zipcode) {
        String url = "https://team4-tcss450-project-server.herokuapp.com/favorites/delete";
        JSONObject body = new JSONObject();
        try {
            // all of the data that is being passed to the backend.
            body.put("memberid", memberid);
            body.put("zipcode", zipcode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // pushes the data to the backend.
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
                this::handleError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // sending a request to connect to the backend.
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }
    /**
     * This methods deletes a location.
     * @param memberid
     * @param zipcode
     * @param lat
     * @param lon
     * @param city
     * @param state
     */
    public void connect(final int memberid, String zipcode,String lat, String lon, String city, String state) {
        String url = "https://team4-tcss450-project-server.herokuapp.com/favorites/add";
        JSONObject body = new JSONObject();
        try {
            // all of the data that is being passed to the backend.
            body.put("memberid", memberid);
            body.put("zipcode", zipcode);
            body.put("lat", lat);
            body.put("lon", lon);
            body.put("city", city);
            body.put("state", state);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // pushes the data to the backend.
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
                this::handleError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // sending a request to connect to the backend.
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }

}
