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
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchContactsViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mNewContactsResponse;
    private MutableLiveData<JSONObject> mExistingContactsResponse;

    public SearchContactsViewModel(@NonNull Application application) {
        super(application);
        mNewContactsResponse = new MutableLiveData<>();
        mExistingContactsResponse = new MutableLiveData<>();
    }

    public void addNewContactsResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mNewContactsResponse.observe(owner, observer);
    }

    public void addExistingContactsResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mExistingContactsResponse.observe(owner, observer);
    }

    public void searchNew(final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/search/new/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("SearchNewContactsRequestBody", body.toString());

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                mNewContactsResponse::setValue,
                this::handleSearchNewError) {

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

    public void searchExisting(final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts/search/existing/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                mExistingContactsResponse::setValue,
                this::handleSearchExistingError) {

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

    private void handleSearchExistingError(final VolleyError error) {
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
                Log.e("JSONException", "Found in handSearchExistingError");
            }
            mExistingContactsResponse.setValue(response);
        }
    }

    private void handleSearchNewError(final VolleyError error) {
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
                Log.e("JSONException", "Found in handleSearchNewError");
            }
            mNewContactsResponse.setValue(response);
        }
    }
}
