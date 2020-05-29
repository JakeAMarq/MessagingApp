package edu.uw.tcss450.team4projectclient.ui.chatrooms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
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

    private final ChatRoomViewModel mChatRoomModel;

    private final UserInfoViewModel mUserModel;

    /**
     * Creates an instance of ChatRoomRecyclerViewAdapter with a list of chat rooms
     * @param chatRooms the chat rooms
     */
    public ChatRoomRecyclerViewAdapter(List<ChatRoom> chatRooms, Context context) {
        this.mChatRooms = chatRooms;
        this.mCtx = context;
        ViewModelProvider provider = new ViewModelProvider((FragmentActivity) mCtx);
        mChatRoomModel = provider.get(ChatRoomViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
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
                if (chatRoom.getOwner().equals(mUserModel.getEmail())) {
                    popup.inflate(R.menu.chat_room_owner_menu);
                } else {
                    popup.inflate(R.menu.chat_room_menu);
                }

                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_item_leave_chat_room:
                            buildLeaveChatDialog(mCtx, chatRoom.getId()).show();
                            break;
                        case R.id.menu_item_add_user_to_chat_room:
                            // TODO: Add feedback when user is added
                            buildAddUserToChatRoomDialog(mCtx, chatRoom.getId()).show();
                            break;
                        case R.id.menu_item_remove_user_from_chat_room:
                            // TODO: Add feedback when user is removed
                            buildRemoveUserFromChatRoomDialog(mCtx, chatRoom.getId()).show();
                            break;
                        case R.id.menu_item_delete_chat_room:
                            buildDeleteChatDialog(mCtx, chatRoom.getId()).show();
                            break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();
            });
            binding.textChatRoomTitle.setText(chatRoom.getName());
            binding.textLastMessage.setText(chatRoom.getLastMessage());
        }

        private AlertDialog.Builder buildLeaveChatDialog(final Context c, final int chatId) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to leave the chat room?");

            builder.setPositiveButton("YES", (dialog, i) -> mChatRoomModel.removeUserFromChatRoom(chatId, mUserModel.getEmail(), mUserModel.getJwt()));

            builder.setNegativeButton("NO", (dialogInterface, i) -> {});

            return builder;
        }

        private AlertDialog.Builder buildDeleteChatDialog(final Context c, final int chatId) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to delete this chat room? This cannot be reversed.");

            builder.setPositiveButton("YES", (dialog, i) -> mChatRoomModel.deleteChatRoom(chatId, mUserModel.getJwt()));

            builder.setNegativeButton("NO", (dialogInterface, i) -> {});

            return builder;
        }

        private AlertDialog.Builder buildAddUserToChatRoomDialog(final Context c, final int chatId){
            LayoutInflater inflater = LayoutInflater.from(c);
            View subView = inflater.inflate(R.layout.dialog_add_user_to_chat_room, null);
            final EditText subEditText = (EditText)subView.findViewById(R.id.edit_user_email);

            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Add user to chat room");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();

            builder.setPositiveButton("Add", (dialog, which) -> mChatRoomModel.addUserToChatRoom(chatId, subEditText.getText().toString(), mUserModel.getJwt()));

            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });

            return builder;
        }

        private AlertDialog.Builder buildRemoveUserFromChatRoomDialog(final Context c, final int chatId){
            LayoutInflater inflater = LayoutInflater.from(c);
            View subView = inflater.inflate(R.layout.dialog_add_user_to_chat_room, null);
            final EditText subEditText = (EditText)subView.findViewById(R.id.edit_user_email);

            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Remove user from chat room");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();

            builder.setPositiveButton("Remove", (dialog, which) -> mChatRoomModel.removeUserFromChatRoom(chatId, subEditText.getText().toString(), mUserModel.getJwt()));

            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });

            return builder;
        }
    }

}
