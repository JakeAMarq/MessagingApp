package com.JakeAMarq.MessagingApp.ui.chatrooms;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentChatRoomListBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.chat.MessageViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.AddRemoveUsersViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.AddDeleteChatsViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.GetChatsViewModel;

/**
 * Conversations page where user can see and navigate to multiple chat rooms
 */
public class ChatRoomListFragment extends Fragment {

    /**
     * Map where keys are chat room IDs and values are their corresponding ChatRoom object
     */
    private Map<Integer, ChatRoom> mChatRooms;

    /**
     * The RecyclerView displaying the chat rooms
     */
    private RecyclerView mRecyclerView;

    /**
     * ViewModel used to store user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    /**
     * ViewModel used to retrieve messages from server
     */
    private MessageViewModel mMessageModel;

    /**
     * ViewModel used to retrieve chat rooms the user is in
     */
    private GetChatsViewModel mGetChatsModel;

    /**
     * ViewModel used to create and delete chat rooms the user owns
     */
    private AddDeleteChatsViewModel mAddDeleteChatsModel;

    /**
     * ViewModel used to add users to and remove users from chat rooms the user owns
     */
    private AddRemoveUsersViewModel mAddRemoveUsersModel;

    /**
     * Required empty public constructor
     */
    public ChatRoomListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatRooms = new HashMap<>();
        mUserModel = provider.get(UserInfoViewModel.class);
        mMessageModel = provider.get(MessageViewModel.class);
        mGetChatsModel = provider.get(GetChatsViewModel.class);
        mAddDeleteChatsModel = provider.get(AddDeleteChatsViewModel.class);
        mAddRemoveUsersModel = provider.get(AddRemoveUsersViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_chat_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = FragmentChatRoomListBinding.bind(view).listRoot;

        mGetChatsModel.addObserver(getViewLifecycleOwner(), this::observeGetChatRoomsResponse);
        mAddDeleteChatsModel.addAddChatResponseObserver(getViewLifecycleOwner(), this::observeAddChatResponse);
        mAddDeleteChatsModel.addDeleteChatResponseObserver(getViewLifecycleOwner(), this::observeDeleteChatResponse);
        mAddRemoveUsersModel.addAddUserResponseObserver(getViewLifecycleOwner(), this::observeAddUserToChatResponse);
        mAddRemoveUsersModel.addRemoveUserResponseObserver(getViewLifecycleOwner(), this::observeRemoveUserFromChatResponse);

        mGetChatsModel.getChatRooms(mUserModel.getJwt());

        updateMessages();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_chat_room:
                showAddChatRoomDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_conversations, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Adds a new ChatRoom to mChatRooms, setups up a message observer for it in mMessageModel, and
     * retrieves the first messages from it
     * @param chatRoom the ChatRoom
     */
    private void addChatRoom(ChatRoom chatRoom) {
        mChatRooms.put(chatRoom.getId(), chatRoom);
        mMessageModel.getOrCreateMapEntry(chatRoom.getId()).removeObservers(getViewLifecycleOwner());
        mMessageModel.addMessageObserver(chatRoom.getId(),
                                            getViewLifecycleOwner(),
                                            response -> {
                                                mChatRooms.get(chatRoom.getId()).setMessages(mMessageModel.getMessageListByChatId(chatRoom.getId()));
                                                updateMessages();
                                            });
        mMessageModel.getFirstMessages(chatRoom.getId(), mUserModel.getJwt());
    }

    /**
     * Observer function that triggers when request from mGetChatsModel gets a response
     * @param response the JSONObject response returned from request in mGetChatsModel
     */
    private void observeGetChatRoomsResponse(final JSONObject response) {
        try {
            if (response.has("rows")) {
                mChatRooms.clear();
                JSONArray chatRooms = response.getJSONArray("rows");
                for(int i = 0; i < chatRooms.length(); i++) {
                    JSONObject message = chatRooms.getJSONObject(i);
                    int chatId = message.getInt("chatid");
                    ChatRoom chatRoom = new ChatRoom(
                            chatId,
                            message.getString("name"),
                            message.getString("email")
                    );
                    mChatRooms.put(chatId, chatRoom);
                }

                mMessageModel.clearChatRooms();
                for (int chatId : mChatRooms.keySet()) {
                    mMessageModel.addMessageObserver(chatId,
                            getViewLifecycleOwner(),
                            theResponse -> {
                                mChatRooms.get(chatId).setMessages(mMessageModel.getMessageListByChatId(chatId));
                                updateMessages();
                            });
                    mMessageModel.getFirstMessages(chatId, mUserModel.getJwt());
                }
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error retrieving chats: " + response.getString("error"), Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeGetChatRoomsResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred retrieving chats from server", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Observer function that triggers when request to create a chat from mAddDeleteChatsModel gets a response
     * @param response the JSONObject response returned from request to create a chat in mAddDeleteChatsModel
     */
    private void observeAddChatResponse(final JSONObject response) {
        try {
            if (response.has("success")) {
                ChatRoom chatRoom = new ChatRoom(
                        response.getInt("chatId"),
                        response.getString("chatName"),
                        mUserModel.getEmail()
                );
                addChatRoom(chatRoom);

//                Toast.makeText(getContext(), "New chat: " + chatRoom.getName() + " added successfully", Toast.LENGTH_LONG).show();
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error creating chat: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to create new chat", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeAddChatResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to create new chat", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Observer function that triggers when request to delete a chat from mAddDeleteChatsModel gets a response
     * @param response the JSONObject response returned from request to delete a chat in mAddDeleteChatsModel
     */
    private void observeDeleteChatResponse(final JSONObject response) {
        try {
            if (response.has("success")) {
                int chatId = response.getInt("chatId");
                if (mChatRooms.containsKey(chatId)) {
                    mChatRooms.remove(chatId);
                    updateMessages();
                }
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error deleting chat: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to delete chat", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeDeleteChatResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to delete chat", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Observer function that triggers when request to add a user to a chat from mAddRemoveUsersModel gets a response
     * @param response the JSONObject response returned from request to add a user to a chat in mAddRemoveUsersModel
     */
    private void observeAddUserToChatResponse(final JSONObject response) {
        try {
            if (response.has("success")) {
                Toast.makeText(getActivity(), "User added to chat successfully", Toast.LENGTH_LONG).show();
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error adding user to chat: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Unknown error occurred attempting to add user to chat", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e){
            Log.e("JSON PARSE ERROR", "Found in observeAddUserToChatResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getActivity(), "Unknown error occurred attempting to add user to chat", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Observer function that triggers when request to remove a user from a chat from mAddRemoveUsersModel gets a response
     * @param response the JSONObject response returned from request to remove a user from a chat in mAddRemoveUsersModel
     */
    private void observeRemoveUserFromChatResponse(final JSONObject response) {
        try {
            if (response.has("success")) {
                if (response.getString("email").equals(mUserModel.getEmail())) {
                    int chatId = response.getInt("chatId");
                    if (mChatRooms.containsKey(chatId)) {
                        mChatRooms.remove(chatId);
                        updateMessages();
                    }
                } else {
                    Toast.makeText(getContext(), "User removed from chat successfully", Toast.LENGTH_LONG).show();
                }
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error removing user from chat: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to remove user from chat", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e){
            Log.e("JSON PARSE ERROR", "Found in observeRemoveUserFromChatResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to remove user from chat", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Refreshes the RecyclerView by attaching an entirely new adapter to it
     */
    public void updateMessages() {
        List<ChatRoom> chatRooms = new ArrayList<>(mChatRooms.values());
        chatRooms.sort((ChatRoom c1, ChatRoom c2) -> c2.getLastTimeStamp().compareTo(c1.getLastTimeStamp()));
        mRecyclerView.setAdapter(new ChatRoomRecyclerViewAdapter(chatRooms, getActivity()));
    }

    /**
     * Shows an AlertDialog that asks the user from the name of the chat room they wish to create and sends the
     * request if they click 'Add'
     */
    private void showAddChatRoomDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View subView = inflater.inflate(R.layout.dialog_add_chat_room, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.edit_chat_room_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a chat room");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Add", (dialog, which) -> mAddDeleteChatsModel.addChatRoom(subEditText.getText().toString(), mUserModel.getJwt()));

        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        builder.show();
    }


}
