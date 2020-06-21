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

public class GetContactsViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mContactsResponse;
    private MutableLiveData<JSONObject> mIncomingResponse;
    private MutableLiveData<JSONObject> mOutgoingResponse;

    public GetContactsViewModel(@NonNull Application application) {
        super(application);
        mContactsResponse = new MutableLiveData<>();
        mIncomingResponse = new MutableLiveData<>();
        mOutgoingResponse = new MutableLiveData<>();
    }

    public void addContactsResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mContactsResponse.observe(owner, observer);
    }
    public void addIncomingResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mIncomingResponse.observe(owner, observer);
    }
    public void addOutgoingResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mOutgoingResponse.observe(owner, observer);
    }

    public void getContacts(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                mContactsResponse::setValue,
                this::handleContactsError) {

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

    public void getIncoming(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/incoming/";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                mIncomingResponse::setValue,
                this::handleIncomingError) {

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

    public void getOutgoing(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/outgoing/";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                mOutgoingResponse::setValue,
                this::handleOutgoingError) {

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

    public void handleContactsError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        } else {
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
            mContactsResponse.setValue(response);
        }
    }
    // TODO: Check error handling methods
    public void handleIncomingError(final VolleyError error) {
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
                response.put("error", new JSONObject(data).getString("message"));
            } catch (JSONException e) {
                Log.e("JSONException", "Found in handleIncomingError");
            }
            mIncomingResponse.setValue(response);
        }
    }


    public void handleOutgoingError(final VolleyError error) {
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
                response.put("error", new JSONObject(data).getString("message"));
            } catch (JSONException e) {
                Log.e("JSONException", "Found in handleOutgoingError");
            }
            mOutgoingResponse.setValue(response);
        }
    }
}
