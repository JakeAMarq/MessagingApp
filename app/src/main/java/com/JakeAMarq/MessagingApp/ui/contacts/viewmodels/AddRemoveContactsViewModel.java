package com.JakeAMarq.MessagingApp.ui.contacts.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.io.RequestQueueSingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddRemoveContactsViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mAddResponse;

    private MutableLiveData<JSONObject> mAcceptResponse;

    private MutableLiveData<JSONObject> mDeleteResponse;

    public AddRemoveContactsViewModel(@NonNull Application application) {
        super(application);
        mAddResponse = new MutableLiveData<>();
        mAcceptResponse = new MutableLiveData<>();
        mDeleteResponse = new MutableLiveData<>();
    }

    public void addAddContactResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mAddResponse.observe(owner, observer);
    }

    public void addAcceptContactResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mAcceptResponse.observe(owner, observer);
    }

    public void addDeleteContactResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mDeleteResponse.observe(owner, observer);
    }

    public void addContact(final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/add/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                mAddResponse::setValue,
                this::handleAddContactError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void acceptContact(final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/accept/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                mAcceptResponse::setValue,
                this::handleAcceptContactError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void deleteContact(final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/delete/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                mDeleteResponse::setValue,
                this::handleDeleteContactError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * Handles error returned from request in addUserToChatRoom
     * @param error VolleyError returned from request in addUserToChatRoom
     */
    private void handleAddContactError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
            JSONObject response = new JSONObject();
            try {
                if (!data.contains("SQL Error on select from push token"))
                    response.put("error", new JSONObject(data).getString("message"));
                else
                    // User was successfully added, but there was a problem sending them a notification
                    response.put("success", true);
            } catch (JSONException e) {
                Log.e("JSONException", "Found in handleAddUserError");
            }
            mAddResponse.setValue(response);
        }
    }

    /**
     * Handles error returned from request in addUserToChatRoom
     * @param error VolleyError returned from request in addUserToChatRoom
     */
    private void handleAcceptContactError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
            JSONObject response = new JSONObject();
            try {
                if (!data.contains("SQL Error on select from push token"))
                    response.put("error", new JSONObject(data).getString("message"));
                else
                    // User was successfully added, but there was a problem sending them a notification
                    response.put("success", true);
            } catch (JSONException e) {
                Log.e("JSONException", "Found in handleAddUserError");
            }
            mAcceptResponse.setValue(response);
        }
    }

    /**
     * Handles error returned from request in addUserToChatRoom
     * @param error VolleyError returned from request in addUserToChatRoom
     */
    private void handleDeleteContactError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
            JSONObject response = new JSONObject();
            try {
                if (!data.contains("SQL Error on select from push token"))
                    response.put("error", new JSONObject(data).getString("message"));
                else
                    // User was successfully added, but there was a problem sending them a notification
                    response.put("success", true);
            } catch (JSONException e) {
                Log.e("JSONException", "Found in handleAddUserError");
            }
            mDeleteResponse.setValue(response);
        }
    }
}
