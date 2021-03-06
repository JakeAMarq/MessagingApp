package com.JakeAMarq.MessagingApp.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JakeAMarq.MessagingApp.MainActivity;
import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentChatBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.ChatRoom;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    /**
     * The chat room that the fragment is displaying
     */
    private ChatRoom mChatRoom;

    /**
     * The ViewModel that handles sending messages
     */
    private ChatSendViewModel mSendModel;

    /**
     * The ViewModel that handles retrieving messages
     */
    private MessageViewModel mChatModel;

    /**
     * The ViewModel containing the user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());

        mSendModel = provider.get(ChatSendViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(MessageViewModel.class);
        mChatRoom = ChatFragmentArgs.fromBundle(getArguments()).getChatRoom();
        mChatModel.getFirstMessages(mChatRoom.getId(), mUserModel.getJwt());

        ((MainActivity) getActivity())
                .setActionBarTitle(mChatRoom.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatBinding binding = FragmentChatBinding.bind(getView());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerMessages;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        rv.setAdapter(new ChatRecyclerViewAdapter(
                mChatRoom.getMessages(),
                mUserModel.getEmail()));


        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(mChatRoom.getId(), mUserModel.getJwt());
        });

//        mChatModel.addMessageObserver(HARD_CODED_CHAT_ID, getViewLifecycleOwner(),
        mChatModel.addMessageObserver(mChatRoom.getId(), getViewLifecycleOwner(),
                list -> {
                    // TODO: Find solution for scroll position
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatRoom.getId(),
                    mUserModel.getJwt(),
                    binding.editMessage.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));
    }
}

