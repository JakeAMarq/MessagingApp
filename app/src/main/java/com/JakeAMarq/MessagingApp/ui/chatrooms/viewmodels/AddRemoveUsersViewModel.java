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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.io.RequestQueueSingleton;

/**
 * ViewModel that handles the requests to the server for adding users to chat rooms
 * and removing users from chat rooms
 */
public class AddRemoveUsersViewModel extends AndroidViewModel {

    /**
     * MutableLiveData used to store response from server when requesting to add a user to a chat room
     */
    private MutableLiveData<JSONObject> mAddUserResponse;

    /**
     * MutableLiveData used to store response from server when requesting to remove a user from a chat room
     */
    private MutableLiveData<JSONObject> mRemoveUserResponse;

    /**
     * Creates an instance of AddRemoveUsersViewModel
     * @param application Application object
     */
    public AddRemoveUsersViewModel(@NonNull Application application) {
        super(application);
        mAddUserResponse = new MutableLiveData<>();
        mRemoveUserResponse = new MutableLiveData<>();
    }

    /**
     * Adds observer to mAddUserResponse
     * @param owner LifecyclerOwner
     * @param observer Observer function
     */
    public void addAddUserResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mAddUserResponse.observe(owner, observer);
    }

    /**
     * Adds observer to mRemoveUserResponse
     * @param owner LifecyclerOwner
     * @param observer Observer function
     */
    public void addRemoveUserResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mRemoveUserResponse.observe(owner, observer);
    }

    /**
     * Sends request to server to add a specific user to a specific chat room
     * @param chatId the ID of the chat room to which the user will be added
     * @param email the email of the user being added
     * @param jwt the JWT of the user sending the request
     */
    public void addUserToChatRoom(final int chatId, final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/";

        JSONObject body = new JSONObject();
        try {
            body.put("chatId", chatId);
            body.put("user", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                this::setAddUserResponse,
                this::handleAddUserError) {

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
     * Sends request to server to remove a specific user from a specific chat room
     * @param chatId the ID of the chat room from which the user will be removed
     * @param user the user of the user being removed
     * @param jwt the JWT of the user sending the request
     */
    public void removeUserFromChatRoom(final int chatId, final String user, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" +
                chatId + "/" +
                user;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                this::setRemoveUserResponse,
                this::handleRemoveUserError) {

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
     * Sets value of mAddUserResponse
     * @param response the new value
     */
    private void setAddUserResponse(final JSONObject response) {
        mAddUserResponse.setValue(response);
    }

    /**
     * Sets value of mRemoveUserResponse
     * @param response the new value
     */
    private void setRemoveUserResponse(final JSONObject response) {
        mRemoveUserResponse.setValue(response);
    }

    /**
     * Handles error returned from request in addUserToChatRoom
     * @param error VolleyError returned from request in addUserToChatRoom
     */
    private void handleAddUserError(final VolleyError error) {
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
            mAddUserResponse.setValue(response);
        }
    }

    /**
     * Handles error returned from request in removeUserFromChatRoom
     * @param error VolleyError returned from request in removeUserFromChatRoom
     */
    private void handleRemoveUserError(final VolleyError error) {
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
                Log.e("JSONException", "Found in handleRemoveUserError");
            }
            mRemoveUserResponse.setValue(response);
        }
    }
}
