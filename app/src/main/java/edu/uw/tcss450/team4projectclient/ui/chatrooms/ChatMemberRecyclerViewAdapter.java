package edu.uw.tcss450.team4projectclient.ui.chatrooms;

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
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.MainActivity;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.team4projectclient.databinding.FragmentEditChatCardBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.chatrooms.viewmodels.AddRemoveUsersViewModel;

public class ChatMemberRecyclerViewAdapter extends RecyclerView.Adapter<ChatMemberRecyclerViewAdapter.ChatMemberViewHolder> {

    private int mChatId;

    private List<String> mMemberEmails;

    private Context mCtx;

    private AddRemoveUsersViewModel mAddRemoveUsersModel;

    private UserInfoViewModel mUserInfoModel;

    public ChatMemberRecyclerViewAdapter(final List<String> memberEmails, final int chatId, final Context context) {
        mMemberEmails = memberEmails;
        mChatId = chatId;
        mCtx = context;
        ViewModelProvider provider = new ViewModelProvider((FragmentActivity) mCtx);
        mAddRemoveUsersModel = provider.get(AddRemoveUsersViewModel.class);
        mUserInfoModel = provider.get(UserInfoViewModel.class);
    }

    @NonNull
    @Override
    public ChatMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatMemberViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_edit_chat_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMemberViewHolder holder, int position) {
        holder.setChatMember(mMemberEmails.get(position));
    }

    @Override
    public int getItemCount() {
        return mMemberEmails.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the ChatRoomRecyclerView.
     */
    public class ChatMemberViewHolder extends RecyclerView.ViewHolder {

        /**
         * The View of the chat card
         */
        public final View mView;

        /**
         * The viewbinding of the chat card
         */
        public FragmentEditChatCardBinding binding;

        /**
         * Creates an instance of ChatRoomViewHolder
         * @param view the View
         */
        public ChatMemberViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentEditChatCardBinding.bind(view);
        }

        /**
         * Sets up the chat card by setting up the TextViews, the options menu, and the click listeners
         * @param email the ChatRoom that the chat card represents
         */
        void setChatMember(final String email) {

            binding.textContactName.setText(email);

            // Setting up options menu
            binding.buttonRemoveChatMember.setOnClickListener(view -> showRemoveUserDialog(mCtx, email));
        }

        /**
         * Shows AlertDialog asking user to confirm they want to leave a chat room
         * @param context Context
         * @param email ID of the chat room the user is trying to leave
         */
        private void showRemoveUserDialog(final Context context, final String email) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to remove " + email + " from the chat room?");

            builder.setPositiveButton("YES", (dialog, i) -> mAddRemoveUsersModel.removeUserFromChatRoom(mChatId, email, mUserInfoModel.getJwt()));

            builder.setNegativeButton("NO", (dialogInterface, i) -> {});

            builder.show();
        }


    }
}
