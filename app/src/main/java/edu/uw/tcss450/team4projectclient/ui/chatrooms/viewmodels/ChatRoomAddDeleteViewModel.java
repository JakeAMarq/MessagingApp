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

public class ChatRoomAddDeleteViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mAddChatResponse;

    private MutableLiveData<JSONObject> mDeleteChatResponse;

    public ChatRoomAddDeleteViewModel(@NonNull Application application) {
        super(application);
        mAddChatResponse = new MutableLiveData<>();
        mDeleteChatResponse = new MutableLiveData<>();
    }

    public void addAddChatResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mAddChatResponse.observe(owner, observer);
    }

    public void addDeleteChatResponseObserver(LifecycleOwner owner, Observer<? super JSONObject> observer) {
        mDeleteChatResponse.observe(owner, observer);
    }

    /**
     *
     */
    public void addChatRoom(final String chatRoomName, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/";

        JSONObject body = new JSONObject();
        try {
            body.put("name", chatRoomName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //no body for this get request
                this::setAddChatResponse,
                this::handleAddChatError) {

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
     *
     */
    public void deleteChatRoom(final int chatId, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" + chatId;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                this::setDeleteChatResponse,
                this::handleDeleteChatError) {

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

    private void setAddChatResponse(final JSONObject response) {
        mAddChatResponse.setValue(response);
    }

    private void setDeleteChatResponse(JSONObject response) {
        mDeleteChatResponse.setValue(response);
    }

    /**
     * Handles error in request in getChatIds
     * @param error VolleyError returned from request in getChatIds
     */
    private void handleAddChatError(final VolleyError error) {
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
                Log.e("JSON Exception", "Found in handleAddChatError");
            }
            mAddChatResponse.setValue(response);
        }
    }

    /**
     * Handles error in request in getChatIds
     * @param error VolleyError returned from request in getChatIds
     */
    private void handleDeleteChatError(final VolleyError error) {
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
                Log.e("JSON Exception", "Found in handleDeleteChatError");
            }
            mDeleteChatResponse.setValue(response);
        }
    }
}
