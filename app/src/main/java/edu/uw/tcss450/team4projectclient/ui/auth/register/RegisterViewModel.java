/**
 * Team 4
 * This class helps a user register for a new account.
 */

package edu.uw.tcss450.team4projectclient.ui.auth.register;

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

public class RegisterViewModel extends AndroidViewModel {

    // keeps track of all fo the JSONObject responses.
    private MutableLiveData<JSONObject> mResponse;

    /**
     * constructor that initializes a mutable live data object.
     * @param application
     */
    public RegisterViewModel(@NonNull Application application) {
        super(application);

        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * adding an observer for the register class.
     * @param owner
     * @param observer
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * This method gets called in case an error happens during the registration process.
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
     * This methods registers a user.
     * @param first the user first name
     * @param last the user last name
     * @param email the user's email
     * @param password the user's password
     * @param userName the user's username/nickname
     * @param verification, the user's verification code when they sign in ot verify email
     */
    public void connect(final String first,
                        final String last,
                        final String email,
                        final String password,
                        final String userName,
                        final String verification) {
        String url = "https://team4-tcss450-project-server.herokuapp.com/auth";    // need back-end set up
        System.out.println(verification);
        JSONObject body = new JSONObject();
        try {
            // all of the data that is being passed to the backend.
            body.put("first", first);
            body.put("last", last);
            body.put("email", email);
            body.put("password", password);
            body.put("username", userName);
            body.put("verification", verification);
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
