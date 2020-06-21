package com.JakeAMarq.MessagingApp.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentContactsBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.ChatRoom;
import com.JakeAMarq.MessagingApp.ui.contacts.viewmodels.AddRemoveContactsViewModel;
import com.JakeAMarq.MessagingApp.ui.contacts.viewmodels.GetContactsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Contact> mContacts, mIncomingRequests, mOutgoingRequests;
    private UserInfoViewModel mUserInfoModel;
    private GetContactsViewModel mGetContactsModel;

    public ContactsFragment() {
        // Required empty public constructor
        mContacts = new ArrayList<>();
        mIncomingRequests = new ArrayList<>();
        mOutgoingRequests = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = FragmentContactsBinding.bind(view).listRoot;
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        mUserInfoModel = provider.get(UserInfoViewModel.class);
        mGetContactsModel = provider.get(GetContactsViewModel.class);
        mGetContactsModel.addContactsResponseObserver(getViewLifecycleOwner(), this::observeGetContactsResponse);
        mGetContactsModel.addIncomingResponseObserver(getViewLifecycleOwner(), this::observeGetIncomingResponse);
        mGetContactsModel.addOutgoingResponseObserver(getViewLifecycleOwner(), this::observeGetOutgoingResponse);

        mGetContactsModel.getContacts(mUserInfoModel.getJwt());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_new_contact:
                navigateToAddContact(getView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void navigateToAddContact(View view) {
        Navigation.findNavController(view).navigate(
                ContactsFragmentDirections
                        .actionNavigationContactsToAddContactFragment());
    }

    private void observeGetContactsResponse(final JSONObject response) {
        try {
            if (response.has("rows")) {
                mContacts.clear();
                JSONArray contacts = response.getJSONArray("rows");
                for(int i = 0; i < contacts.length(); i++) {
                    JSONObject contactJSON = contacts.getJSONObject(i);
                    Contact contact = new Contact(
                            contactJSON.getString("firstname"),
                            contactJSON.getString("lastname"),
                            contactJSON.getString("username"),
                            contactJSON.getString("email")
                    );
                    mContacts.add(contact);
                }
                mRecyclerView.setAdapter(new ContactRecyclerViewAdapter(mContacts, getContext()));
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error retrieving contacts: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve contacts", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeGetChatRoomsResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve contacts", Toast.LENGTH_LONG).show();
        }
    }

    private void observeGetIncomingResponse(final JSONObject response) {
        try {
            if (response.has("rows")) {
                mIncomingRequests.clear();
                JSONArray contacts = response.getJSONArray("rows");
                for(int i = 0; i < contacts.length(); i++) {
                    JSONObject contactJSON = contacts.getJSONObject(i);
                    Contact contact = new Contact(
                            contactJSON.getString("firstname"),
                            contactJSON.getString("lastname"),
                            contactJSON.getString("username"),
                            contactJSON.getString("email")
                    );
                    mIncomingRequests.add(contact);
                }
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error retrieving incoming contact requests: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve incoming contact requests", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeGetChatRoomsResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve incoming contact requests", Toast.LENGTH_LONG).show();
        }
    }

    private void observeGetOutgoingResponse(final JSONObject response) {
        try {
            if (response.has("rows")) {
                mOutgoingRequests.clear();
                JSONArray contacts = response.getJSONArray("rows");
                for(int i = 0; i < contacts.length(); i++) {
                    JSONObject contactJSON = contacts.getJSONObject(i);
                    Contact contact = new Contact(
                            contactJSON.getString("firstname"),
                            contactJSON.getString("lastname"),
                            contactJSON.getString("username"),
                            contactJSON.getString("email")
                    );
                    mOutgoingRequests.add(contact);
                }
            } else if (response.has("error")) {
                Toast.makeText(getContext(), "Error retrieving outgoing contact requests: " + response.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve outgoing contact requests", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in observeGetChatRoomsResponse");
            Log.e("JSON PARSE ERROR", "Message: " + e.getMessage());
            Toast.makeText(getContext(), "Unknown error occurred attempting to retrieve outgoing contact requests", Toast.LENGTH_LONG).show();
        }
    }
}
