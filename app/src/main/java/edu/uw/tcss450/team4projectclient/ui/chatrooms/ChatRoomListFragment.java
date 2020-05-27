package edu.uw.tcss450.team4projectclient.ui.chatrooms;

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
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatRoomListBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.chat.MessageViewModel;

/**
 * Conversations page where user can see and navigate to multiple chat rooms
 */
public class ChatRoomListFragment extends Fragment {

    /**
     * ChatViewModel containing a map of chatIds and list of messages for respective chat rooms
     * Used to get messages from server
     */
    private MessageViewModel mMessageModel;

    /**
     * UserInfoViewModel containing user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    private ChatRoomViewModel mChatRoomModel;

    private RecyclerView mRecyclerView;

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
        mMessageModel = provider.get(MessageViewModel.class);
        mChatRoomModel = provider.get(ChatRoomViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
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
        mChatRoomModel.addObserver(getViewLifecycleOwner(), integerStringMap -> {
            Log.d("ChatRoomListFragment", "Chat room observer called");
            mMessageModel.clearChatRooms();
            for (int chatId : integerStringMap.keySet()) {
                mMessageModel.addMessageObserver(chatId,
                        getViewLifecycleOwner(),
                        response -> updateMessages());
                mMessageModel.getFirstMessages(chatId, mUserModel.getJwt());
            }
            if (integerStringMap.isEmpty()) updateMessages();
        });

        mChatRoomModel.getChatIds(mUserModel.getJwt());

        if (view instanceof RecyclerView) {
            ((RecyclerView) view).setAdapter(
                    new ChatRoomRecyclerViewAdapter(mMessageModel.getChatRooms(), getActivity()));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.conversations_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Refreshes the RecyclerView by attaching an entirely new adapter to it
     */
    public void updateMessages() {
        mRecyclerView.setAdapter(new ChatRoomRecyclerViewAdapter(mMessageModel.getChatRooms(), getActivity()));
    }


}
