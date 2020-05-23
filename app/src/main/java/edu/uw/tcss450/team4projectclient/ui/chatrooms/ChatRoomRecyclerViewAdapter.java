package edu.uw.tcss450.team4projectclient.ui.chatrooms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatRoom;

/**
 * RecyclerViewAdapter to display list of chat rooms in ChatRoomListFragment
 */
public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    /**
     * The list of chat rooms being displayed
     */
    private final List<ChatRoom> mChatRooms;

    private final Context mCtx;

    /**
     * Creates an instance of ChatRoomRecyclerViewAdapter with a list of chat rooms
     * @param chatRooms the chat rooms
     */
    public ChatRoomRecyclerViewAdapter(List<ChatRoom> chatRooms, Context context) {
        this.mChatRooms = chatRooms;
        this.mCtx = context;
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

    /**
     * Navigates to ChatFragment containing messages from chatRoom
     * @param view mView from ChatRoomViewHolder
     * @param chatRoom chat room being passed to ChatFragment
     */
    public void navigateToChatRoom(View view, ChatRoom chatRoom) {
        Navigation.findNavController(view).navigate(
                ChatRoomListFragmentDirections
                        .actionNavigationChatRoomsToChatFragment(chatRoom));
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Chat Room Recycler View.
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
            // Makes row clickable
            mView.setOnClickListener(view -> navigateToChatRoom(mView, chatRoom));
            binding.optionsMenuChatRoom.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, this.binding.optionsMenuChatRoom);
                //inflating menu from xml resource
                popup.inflate(R.menu.chat_room_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_item_leave_chat_room:
                            // TODO: Add leave chat room functionality
                            break;
                        case R.id.menu_item_add_user_to_chat_room:
                            // TODO: Add add user functionality
                            break;
                        case R.id.menu_item_remove_user_from_chat_room:
                            // TODO: Add remove user functionality
                            break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();
            });
            binding.textChatRoomTitle.setText("Chat Room ID: " + chatRoom.getChatId());
            binding.textLastMessage.setText(chatRoom.getLastMessage());
        }

    }

}
