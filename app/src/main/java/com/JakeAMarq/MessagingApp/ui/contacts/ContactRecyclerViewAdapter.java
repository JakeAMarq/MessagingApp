package com.JakeAMarq.MessagingApp.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentContactCardBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.contacts.viewmodels.AddRemoveContactsViewModel;

import java.util.List;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    /**
     * The list of chat rooms being displayed
     */
    private List<Contact> mContacts;

    /**
     * The Context of the RecyclerView
     */
    private final Context mCtx;

    /**
     * ViewModel used to store user's email and JWT
     */
    private UserInfoViewModel mUserModel;

    private AddRemoveContactsViewModel mAddRemoveContactsModel;

    /**
     * Creates an instance of ChatRoomRecyclerViewAdapter with a list of chat rooms
     * @param contacts the contacts
     */
    public ContactRecyclerViewAdapter(List<Contact> contacts, Context context) {
        this.mContacts = contacts;
        this.mCtx = context;

        ViewModelProvider provider = new ViewModelProvider((FragmentActivity) mCtx);
        mUserModel = provider.get(UserInfoViewModel.class);
        mAddRemoveContactsModel = provider.get(AddRemoveContactsViewModel.class);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void updateList(List<Contact> contacts) {
        mContacts = contacts;
        this.notifyDataSetChanged();
    }

    /**
     * Navigates to ContactFragment to display contact info
     * @param view mView from ContactViewHolder
     * @param contact Contact being passed to ContactFragment
     */
    private void navigateToContact(View view, Contact contact) {
        Navigation.findNavController(view).navigate(
                ContactsFragmentDirections
                        .actionNavigationContactsToContactFragment(contact));
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the ChatRoomRecyclerView.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        /**
         * The View of the chat card
         */
        public final View mView;

        /**
         * The viewbinding of the contact card
         */
        public FragmentContactCardBinding binding;

        /**
         * Creates an instance of ChatRoomViewHolder
         * @param view the View
         */
        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        /**
         * Sets up the chat card by setting up the TextViews, the options menu, and the click listeners
         * @param contact the ChatRoom that the chat card represents
         */
        void setContact(final Contact contact) {

            binding.textContactName.setText(contact.getUser());

            // Makes row clickable
            mView.setOnClickListener(view -> navigateToContact(mView, contact));

            // Setting up options menu
            binding.optionsMenuContact.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, this.binding.optionsMenuContact);

                popup.inflate(R.menu.menu_contact_card);

                //adding click listener to menu items
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_item_message:
                            // TODO: Add message from contact menu functionality
                            break;
                        case R.id.menu_item_remove_from_contacts:
                            showDeleteContactDialog(mCtx, contact.getUser());
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
         * @param username username of the contact to be deleted
         */
        private void showDeleteContactDialog(final Context context, final String username) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to delete this contact?");

            builder.setPositiveButton("YES", (dialog, i) -> mAddRemoveContactsModel.deleteContact(username, mUserModel.getJwt()));

            builder.setNegativeButton("NO", (dialog, i) -> {});

            builder.show();
        }
    }

}

