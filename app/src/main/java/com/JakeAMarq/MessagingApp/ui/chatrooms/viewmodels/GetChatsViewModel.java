package com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels;

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

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.io.RequestQueueSingleton;

/**
 * ViewModel that handles the requests to the server for retrieving info of all the chat rooms
 * that the user is in
 */
public class GetChatsViewModel extends AndroidViewModel {

    /**
     * MutableLiveData used to store response from server
     */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Creates an instance of GetChatsViewModel
     * @param application Application object
     */
    public GetChatsViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
    }

    /**
     * Adds observer to mChatIds
     * @param owner LifeCycleOwner
     * @param observer Observer function
     */
    public void addObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Requests to get the ID, name, and owner's email of every chat room the user is in from the server
     * @param jwt User's JWT
     */
    public void getChatRooms(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

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
     * Handles successful request from getChatRoomss
     * @param response JSONObject returned from request in getChatRooms
     */
    private void handleSuccess(final JSONObject response) {
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in GetChatsViewModel: " + response);
        }
        mResponse.setValue(response);
    }

    /**
     * Handles error returned from request in getChatRooms
     * @param error VolleyError returned from request in getChatRooms
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

}
