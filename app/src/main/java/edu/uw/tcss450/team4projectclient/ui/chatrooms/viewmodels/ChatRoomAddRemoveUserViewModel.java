package edu.uw.tcss450.team4projectclient.ui.chatrooms.viewmodels;

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

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.io.RequestQueueSingleton;

public class ChatRoomAddRemoveUserViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mAddUserResponse;

    private MutableLiveData<JSONObject> mRemoveUserResponse;

    public ChatRoomAddRemoveUserViewModel(@NonNull Application application) {
        super(application);
        mAddUserResponse = new MutableLiveData<>();
        mRemoveUserResponse = new MutableLiveData<>();
    }

    public void addAddUserResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mAddUserResponse.observe(owner, observer);
    }

    public void addRemoveUserResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mRemoveUserResponse.observe(owner, observer);
    }

    public void addUserToChatRoom(final int chatId, final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/";

        JSONObject body = new JSONObject();
        try {
            body.put("chatId", chatId);
            body.put("email", email);
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

    public void removeUserFromChatRoom(final int chatId, final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" +
                chatId + "/" +
                email;

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

    public void handleAddUserSuccess(final JSONObject response) {
        if (response.has("success")) {
            mAddUserResponse.setValue(response);
        } else {
            Log.e("ChatRoomAddRemoveUserViewModel", "Unexpected response from add user request");
        }
    }

    private void setAddUserResponse(final JSONObject response) {
        mAddUserResponse.setValue(response);
    }

    private void setRemoveUserResponse(final JSONObject response) {
        mRemoveUserResponse.setValue(response);
    }

    public void handleRemoveUserSuccess(final JSONObject response) {
        if (response.has("success")) {
            mRemoveUserResponse.setValue(response);
        } else {
            Log.e("ChatRoomAddRemoveUserViewModel", "Unexpected response from remove user request");
        }
    }

    /**
     * Handles error in request in getChatIds
     * @param error VolleyError returned from request in getChatIds
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
     * Handles error in request in getChatIds
     * @param error VolleyError returned from request in getChatIds
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
