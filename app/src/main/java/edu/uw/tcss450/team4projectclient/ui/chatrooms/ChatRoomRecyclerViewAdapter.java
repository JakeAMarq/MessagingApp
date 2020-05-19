package edu.uw.tcss450.team4projectclient.ui.chatrooms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatRoom;

public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {
    private final List<ChatRoom> mChatRooms;

    public ChatRoomRecyclerViewAdapter(List<ChatRoom> items) {
        this.mChatRooms = items;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_card, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.setChatRoom(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    public void navigateToChatRoom(View view, ChatRoom chatRoom) {
        Navigation.findNavController(view).navigate(
                ChatRoomListFragmentDirections
                        .actionNavigationChatRoomsToChatFragment(chatRoom));
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Chat Recycler View.
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatCardBinding binding;

        public ChatRoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        void setChatRoom(final ChatRoom chatRoom) {
            mView.setOnClickListener(view -> navigateToChatRoom(mView, chatRoom));
            binding.textChatRoomTitle.setText("Chat Room ID: " + chatRoom.getChatId());
            binding.textLastMessage.setText(chatRoom.getLastMessage());
        }

    }

}
