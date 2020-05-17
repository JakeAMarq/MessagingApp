package edu.uw.tcss450.team4projectclient.ui.conversations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatRoomListBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatMessage;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatViewModel;

/**
 * Conversations page
 */
public class ChatRoomListFragment extends Fragment {

    private ChatViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    /**
     * Required empty public constructor
     */
    public ChatRoomListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(ChatViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

//        mChatModel.getFirstMessages(1, mUserModel.getJwt());
//        mChatModel.getFirstMessages(2, mUserModel.getJwt());
//        mChatModel.getFirstMessages(3, mUserModel.getJwt());

        View view = inflater.inflate(R.layout.fragment_chat_room_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Remove hardcoded chatrooms
        // ChatIds for hardcoded chatrooms
        int[] chatIds = new int[]{1, 3, 2};
        for (int chatId : chatIds) {
            mChatModel.addMessageObserver(chatId,
                    getViewLifecycleOwner(),
                    response -> updateMessages());
            mChatModel.getFirstMessages(chatId, mUserModel.getJwt());
        }

        if (view instanceof RecyclerView) {
            ((RecyclerView) view).setAdapter(
                    new ChatRoomRecyclerViewAdapter(mChatModel.getChatRooms()));
        }
    }

    public void updateMessages() {
        final RecyclerView rv = FragmentChatRoomListBinding.bind(getView()).listRoot;
        rv.setAdapter(new ChatRoomRecyclerViewAdapter(mChatModel.getChatRooms()));
    }
}
