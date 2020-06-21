package com.JakeAMarq.MessagingApp.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.FragmentAddContactBinding;
import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;
import com.JakeAMarq.MessagingApp.ui.contacts.viewmodels.SearchContactsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {

    private EditText mSearchField;
    private ImageButton mSearchButton;
    private RecyclerView mRecyclerView;
    private List<Contact> mSearchResults;
    private String mJwt;

    private SearchContactsViewModel mSearchContactsModel;



    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchResults = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentAddContactBinding binding = FragmentAddContactBinding.bind(view);
        mSearchField = binding.editContactSearchParam;
        mSearchButton = binding.buttonContactSearch;
        mRecyclerView = binding.recyclerContactSearchResults;

        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        mSearchContactsModel = provider.get(SearchContactsViewModel.class);
        mJwt = provider.get(UserInfoViewModel.class).getJwt();

        mRecyclerView.setAdapter(new ContactRecyclerViewAdapter(mSearchResults, getContext()));

        // TODO: Make user wait for response before being able to search again
        mSearchButton.setOnClickListener(button -> {
            mSearchContactsModel.searchNew(mSearchField.getText().toString(), mJwt);
        });

        mSearchContactsModel.addNewContactsResponseObserver(getViewLifecycleOwner(), this::observeNewContactsResponse);
    }

    private void observeNewContactsResponse(final JSONObject response) {
        try {
            if (response.has("rows")) {
                mSearchResults.clear();
                JSONArray contacts = response.getJSONArray("rows");
                for(int i = 0; i < contacts.length(); i++) {
                    JSONObject contactJSON = contacts.getJSONObject(i);
                    Contact contact = new Contact(
                            contactJSON.getString("firstname"),
                            contactJSON.getString("lastname"),
                            contactJSON.getString("username"),
                            contactJSON.getString("email")
                    );
                    mSearchResults.add(contact);
                }
                ((ContactRecyclerViewAdapter)mRecyclerView.getAdapter()).updateList(mSearchResults);
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
}
