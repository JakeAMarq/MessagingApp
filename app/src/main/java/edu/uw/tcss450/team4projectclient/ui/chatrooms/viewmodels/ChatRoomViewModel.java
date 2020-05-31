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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.io.RequestQueueSingleton;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatMessage;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatRoom;

/**
 * ViewModel that (for now) only serves to get the list of chatids for the chat rooms the user
 * is a part of
 */
public class ChatRoomViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    public ChatRoomViewModel(@NonNull Application application) {
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
     * Requests to get chatid of every chat room the user is a part of from the server
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
     * Handles successful request from getChatIds
     * @param response JSONObject returned from request in getChatIds
     */
    private void handleSuccess(final JSONObject response) {
        Map<Integer, ChatRoom> chatRooms = new HashMap<>();
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        mResponse.setValue(response);
    }

    /**
     * Handles error in request in getChatIds
     * @param error VolleyError returned from request in getChatIds
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
