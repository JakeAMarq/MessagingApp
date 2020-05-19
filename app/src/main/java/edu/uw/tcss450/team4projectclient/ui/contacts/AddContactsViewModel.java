package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.app.Application;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import edu.uw.tcss450.team4projectclient.R;

public class AddContactsViewModel extends AndroidViewModel {

    // keeps track of all fo the JSONObject responses.
    private MutableLiveData<JSONObject> mResponse;

    /**
     * constructor that initializes a mutable live data object.
     * @param application
     */
    public AddContactsViewModel(@NonNull Application application) {
        super(application);

        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * adding an observer for the contacts class.
     * @param owner
     * @param observer
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * This method gets called in case an error happens during adding the contact.
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
     * This methods adds a contact.
     * @param id_a the first user's id
     * @param id_b the second user's id
     */
    public void connect(final Integer id_a,
                        final Integer id_b) {
        String url = getApplication().getResources().getString(R.string.base_url) + "add_user";
        JSONObject body = new JSONObject();
        try {
            // all of the data that is being passed to the backend.
            body.put("MemberID_A", id_a);
            body.put("MemberID_B", id_b);
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
