package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentContactBinding;
import edu.uw.tcss450.team4projectclient.databinding.FragmentContactsBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;

/**
 * Contact page
 */
public class ContactFragment extends Fragment {

    FragmentContactBinding binding;
    private ContactsViewModel mContactsViewModel;
    private AddContactsViewModel mAddContactsViewModel;
    private int memberId_b;
    private UserInfoViewModel mUserModel;
    /**
     * Required empty public constructor
     */
    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * oncreate method initializes the view models
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactsViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        mAddContactsViewModel = new ViewModelProvider(getActivity()).get(AddContactsViewModel.class);
    }

    /**
     * sets the front-end to the back-end
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        this.binding = FragmentContactBinding.inflate(inflater, container, false);
        this.binding.buttonAddContact.setVisibility(View.INVISIBLE);
        this.binding.wantedUsernameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedFirstNameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedLastNameTextview.setVisibility(View.INVISIBLE);
        this.binding.resultsContactsTextview.setVisibility(View.INVISIBLE);
        return this.binding.getRoot();
    }

    /**
     * Sets all of the buttons onClick listeners
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactsViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        mAddContactsViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponseAdd);
        this.binding.buttonAddContact.setVisibility(View.INVISIBLE);
        this.binding.wantedUsernameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedFirstNameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedLastNameTextview.setVisibility(View.INVISIBLE);
        this.binding.resultsContactsTextview.setVisibility(View.INVISIBLE);
        this.binding.buttonSearchContact.setOnClickListener(button -> verifyAuthWithServer());
        this.binding.buttonAddContact.setOnClickListener(button -> verifyAuthWithServerAdd());
    }

    /**
     * Calls my end point to connect to the server to look up a user.
     */
    private void verifyAuthWithServer() {
        mContactsViewModel.connect(
                binding.editLookupNickname.getText().toString());
    }

    /**
     * Calls my end point to connect to the server to add a user.
     */
    private void verifyAuthWithServerAdd() {
        this.binding.buttonAddContact.setVisibility(View.INVISIBLE);
        this.binding.wantedUsernameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedFirstNameTextview.setVisibility(View.INVISIBLE);
        this.binding.wantedLastNameTextview.setVisibility(View.INVISIBLE);
        this.binding.resultsContactsTextview.setVisibility(View.INVISIBLE);
        if (memberId_b == SignInFragment.memberId_a) {
            binding.editLookupNickname.setError("You cannot be friends with yourself...");
        } else {
            mAddContactsViewModel.connect(
                    mUserModel.getId(),
                    memberId_b
            );
        }

    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to ContactsViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {

        if (response.length() > 0) {
            try {
                JSONArray jsonArray = new JSONArray(response.getString("names"));
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                binding.wantedUsernameTextview.setVisibility(View.VISIBLE);
                binding.buttonAddContact.setVisibility(View.VISIBLE);
                binding.wantedFirstNameTextview.setVisibility(View.VISIBLE);
                binding.wantedLastNameTextview.setVisibility(View.VISIBLE);
                binding.resultsContactsTextview.setVisibility(View.VISIBLE);
                memberId_b = jsonObject.getInt("memberid");
                binding.wantedUsernameTextview.setText("Username: " + jsonObject.getString("username"));
                binding.wantedFirstNameTextview.setText("First Name: " + jsonObject.getString("firstname"));
                binding.wantedLastNameTextview.setText("Last Name: " + jsonObject.getString("lastname"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * This validates the response from the server and checks if it's valid, then it connects the user
     *  if successful
     * @param response the JSONObject response
     */
    private void observeResponseAdd(final JSONObject response) {

        Log.e("oooooo", String.valueOf(response));
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editLookupNickname.setError(
                            "Error Authenticating: " +
                                    new JSONObject(
                                            response.getString("data").
                                                    replace("'", "\"")
                                    ).getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {

                Toast.makeText(getContext(), "You added the given user", Toast.LENGTH_LONG).show();

                //Navigation.findNavController(getView()).navigate(ContactFragmentDirections.actionContactFragmentToNavigationContacts());
            }
        } else {
            Log.e("JSON Response", "No Response");
        }
    }

}
