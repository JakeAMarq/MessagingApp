package com.JakeAMarq.MessagingApp.ui.chatrooms;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
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

import com.JakeAMarq.MessagingApp.MainActivity;
import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentChatCardBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.AddDeleteChatsViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.AddRemoveUsersViewModel;

/**
 * RecyclerViewAdapter to display list of chat rooms in ChatRoomListFragment
 */
public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    /**
     * The list of chat rooms being displayed
     */
    private List<ChatRoom> mChatRooms;

    /**
     * The Context of the RecyclerView
     */
    private final Context mCtx;

    /**
     * ViewModel used to store user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    /**
     * ViewModel used to create and delete chat rooms the user owns
     */
    private AddDeleteChatsViewModel mAddDeleteChatsModel;

    /**
     * ViewModel used to add users to and remove users from chat rooms the user owns
     */
    private AddRemoveUsersViewModel mAddRemoveUsersModel;


    /**
     * Creates an instance of ChatRoomRecyclerViewAdapter with a list of chat rooms
     * @param chatRooms the chat rooms
     */
    public ChatRoomRecyclerViewAdapter(List<ChatRoom> chatRooms, Context context) {
        this.mChatRooms = chatRooms;
        this.mCtx = context;

        ViewModelProvider provider = new ViewModelProvider((FragmentActivity) mCtx);
        mUserModel = provider.get(UserInfoViewModel.class);
        mAddDeleteChatsModel = provider.get(AddDeleteChatsViewModel.class);
        mAddRemoveUsersModel = provider.get(AddRemoveUsersViewModel.class);

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
     * Navigates to ChatFragment to display messages from chatRoom
     * @param view mView from ChatRoomViewHolder
     * @param chatRoom ChatRoom being passed to ChatFragment
     */
    public void navigateToChatRoom(View view, ChatRoom chatRoom) {
        Navigation.findNavController(view).navigate(
                ChatRoomListFragmentDirections
                        .actionNavigationConversationsToChatFragment(chatRoom));
    }

    public void updateChatRooms(List<ChatRoom> chatRooms) {
        mChatRooms = chatRooms;
        this.notifyDataSetChanged();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the ChatRoomRecyclerView.
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        /**
         * The View of the chat card
         */
        public final View mView;

        /**
         * The viewbinding of the chat card
         */
        public FragmentChatCardBinding binding;

        /**
         * Creates an instance of ChatRoomViewHolder
         * @param view the View
         */
        public ChatRoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        /**
         * Sets up the chat card by setting up the TextViews, the options menu, and the click listeners
         * @param chatRoom the ChatRoom that the chat card represents
         */
        void setChatRoom(final ChatRoom chatRoom) {

            // Makes preview message bold if there are unread messages in the chat room
            if (((MainActivity) mCtx).hasUnreadMessages(chatRoom.getId()))
                binding.textLastMessage.setTypeface(null, Typeface.BOLD);

            binding.textChatRoomTitle.setText(chatRoom.getName());
            binding.textLastMessage.setText(chatRoom.getLastMessage());

            // Makes row clickable
            mView.setOnClickListener(view -> navigateToChatRoom(mView, chatRoom));

            // Setting up options menu
            binding.optionsMenuChatRoom.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, this.binding.optionsMenuChatRoom);

                /*
                    If the user is the owner, inflate menu_chat_room_owner (that has additional
                    options), else inflate menu_chat_room (where the only option is to leave the
                    chat room).
                 */
                if (chatRoom.getOwner().equals(mUserModel.getEmail())) {
                    popup.inflate(R.menu.menu_chat_room_owner);
                } else {
                    popup.inflate(R.menu.menu_chat_room);
                }

                //adding click listener to menu items
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_item_leave_chat_room:
                            showLeaveChatDialog(mCtx, chatRoom.getId());
                            break;
                        case R.id.menu_item_add_user_to_chat_room:
                            showAddUserToChatRoomDialog(mCtx, chatRoom.getId());
                            break;
                        case R.id.menu_item_remove_user_from_chat_room:
                            showRemoveUserFromChatRoomDialog(mCtx, chatRoom.getId());
                            break;
                        case R.id.menu_item_delete_chat_room:
                            showDeleteChatDialog(mCtx, chatRoom.getId());
                            break;
                    }
                    return false;
                });
                //displaying the options menu
                popup.show();
            });
        }

        /**
         * Shows AlertDialog asking user to confirm they want to leave a chat room
         * @param context Context
         * @param chatId ID of the chat room the user is trying to leave
         */
        private void showLeaveChatDialog(final Context context, final int chatId) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to leave the chat room?");

            builder.setPositiveButton("YES", (dialog, i) -> mAddRemoveUsersModel.removeUserFromChatRoom(chatId, mUserModel.getEmail(), mUserModel.getJwt()));

            builder.setNegativeButton("NO", (dialogInterface, i) -> {});

            builder.show();
        }

        /**
         * Shows AlertDialog asking user to confirm they want to delete a chat room
         * @param context Context
         * @param chatId ID of the chat room the user is trying to delete
         */
        private void showDeleteChatDialog(final Context context, final int chatId) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to delete this chat room? This cannot be reversed.");

            builder.setPositiveButton("YES", (dialog, i) -> mAddDeleteChatsModel.deleteChatRoom(chatId, mUserModel.getJwt()));

            builder.setNegativeButton("NO", (dialogInterface, i) -> {});

            builder.show();
        }

        /**
         * Shows AlertDialog asking user to enter the email of the user they want to add to their chat room
         * @param context Context
         * @param chatId ID of the chat room the user is trying to add another user to
         */
        private void showAddUserToChatRoomDialog(final Context context, final int chatId){
            LayoutInflater inflater = LayoutInflater.from(context);
            View subView = inflater.inflate(R.layout.dialog_add_user_to_chat_room, null);
            final EditText subEditText = (EditText)subView.findViewById(R.id.edit_user_email);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add user to chat room");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();

            builder.setPositiveButton("Add", (dialog, which) -> mAddRemoveUsersModel.addUserToChatRoom(chatId, subEditText.getText().toString(), mUserModel.getJwt()));

            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });

            builder.show();
        }

        /**
         * Shows AlertDialog asking user to enter the email of the user they want to remove from their chat room
         * @param context Context
         * @param chatId ID of the chat room the user is trying to remove another user from
         */
        private void showRemoveUserFromChatRoomDialog(final Context context, final int chatId){
            LayoutInflater inflater = LayoutInflater.from(context);
            View subView = inflater.inflate(R.layout.dialog_add_user_to_chat_room, null);
            final EditText subEditText = (EditText)subView.findViewById(R.id.edit_user_email);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Remove user from chat room");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();

            builder.setPositiveButton("Remove", (dialog, which) -> mAddRemoveUsersModel.removeUserFromChatRoom(chatId, subEditText.getText().toString(), mUserModel.getJwt()));

            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });

            builder.show();
        }
    }

}
