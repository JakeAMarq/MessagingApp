package edu.uw.tcss450.team4projectclient.ui.chatrooms;

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

    /**
     * List of chatIds.
     */
//    private MutableLiveData<Map<Integer, String>> mChatRooms;

    private MutableLiveData<Map<Integer, ChatRoom>> mChatRooms;

    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mChatRooms = new MutableLiveData<>();
    }

    /**
     * Adds observer to mChatIds
     * @param owner LifeCycleOwner
     * @param observer Observer function
     */
    public void addObserver(LifecycleOwner owner, Observer<? super Map<Integer, ChatRoom>> observer) {
        mChatRooms.observe(owner, observer);
    }

    public List<ChatRoom> getChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        Map<Integer, ChatRoom> map = mChatRooms.getValue();
        if (map != null) {
            chatRooms.addAll(map.values());
        }
        return chatRooms;
    }

    /**
     * Requests to get chatid of every chat room the user is a part of from the server
     * @param jwt User's JWT
     */
    public void getChatIds(final String jwt) {
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

        //code here will run
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
                response -> {
//                    Map<Integer, String> chatRooms = mChatRooms.getValue();
//                    chatRooms.remove(chatId);
//                    mChatRooms.setValue(chatRooms);
//                    getChatIds(jwt);
                },
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

    public void removeUserFromChatRoom(final int chatId, final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" +
                chatId + "/" +
                email;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                response -> {
//                    Map<Integer, String> chatRooms = mChatRooms.getValue();
//                    chatRooms.remove(chatId);
//                    mChatRooms.setValue(chatRooms);
                    getChatIds(jwt);
                },
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
     *
     */
    public void addChatRoom(final String chatRoomName, final String ownerEmail, final String jwt) {
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
                response -> {
                    try {
                        int newChatId = response.getInt("chatID");
                        ChatRoom chatRoom = new ChatRoom(
                                newChatId,
                                chatRoomName,
                                ownerEmail
                        );
                        Map<Integer, ChatRoom> chatRooms = mChatRooms.getValue();
                        chatRooms.put(newChatId, chatRoom);
                        mChatRooms.setValue(chatRooms);
                    } catch (JSONException e) {
                        Log.e("JSON PARSE ERROR", "Found in addChatRoom handleSuccess");
                    }
                },
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
     *
     */
    public void deleteChatRoom(final int chatId, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" + chatId;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                response -> {
                    Map<Integer, ChatRoom> chatRooms = mChatRooms.getValue();
                    chatRooms.remove(chatId);
                    mChatRooms.setValue(chatRooms);
                },
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
        try {
            JSONArray messages = response.getJSONArray("rows");
            for(int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                int chatId = message.getInt("chatid");
                ChatRoom chatRoom = new ChatRoom(
                        chatId,
                        message.getString("name"),
                        message.getString("email")
                );
                chatRooms.put(chatId, chatRoom);
            }
            mChatRooms.setValue(chatRooms);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatRoomViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
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
