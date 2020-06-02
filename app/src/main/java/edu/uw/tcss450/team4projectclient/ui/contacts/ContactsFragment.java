package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import edu.uw.tcss450.team4projectclient.MainActivity;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentContactsBinding;
import edu.uw.tcss450.team4projectclient.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragmentDirections;

/**
 * Contacts page
 */
public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private FetchContactsViewModel mFetchViewModel;
    private ArrayList<String> firstName, lastName, userName;
    public static int postCount = 0;
    private ArrayList<Integer> memberId_b, primaryKey;
    private RecyclerView myView;
    private List<ContactsPost> myContacts;
    private ContactsRecyclerViewAdapter myAdapter;
    public static DeleteContactsViewModel mDeleteContactsViewModel;
    /**
     * The ViewModel containing the user's email and JWT
     */
    private UserInfoViewModel mUserModel;


    /**
     * Required empty public constructor
     */
    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * initializes my arraylist.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFetchViewModel = new ViewModelProvider(getActivity()).get(FetchContactsViewModel.class);
        firstName = new ArrayList<>();
        lastName = new ArrayList<>();
        userName = new ArrayList<>();
        memberId_b = new ArrayList<>();
        myContacts = new ArrayList<>();
        primaryKey = new ArrayList<>();
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mDeleteContactsViewModel = new ViewModelProvider(getActivity()).get(DeleteContactsViewModel.class);
    }

    /**
     * initializes the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        if (view instanceof RecyclerView) {
            myView = (RecyclerView) view;
//            myView.setAdapter(
//                    new ContactsRecyclerViewAdapter(ContactsGenerator.getBlogList()));
        }
        return view;
    }

    /**
     * Sets all of the buttons onClick listeners
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);
        mDeleteContactsViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponseDelete);
        verifyAuthWithServer();


    }

    private void observeResponseDelete(final JSONObject response) {
        Log.e("yayy", String.valueOf(response));
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    Log.e("Error",
                            "Error Authenticating: " +
                                    new JSONObject(
                                            response.getString("data").
                                                    replace("'", "\"")
                                    ).getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {

                verifyAuthWithServer();
            }
        } else {
            Log.e("JSON Response", "No Response");
        }
    }


    /**
     * Connects to my end point to grab all of the contacts.
     */
    private void verifyAuthWithServer() {
        mFetchViewModel.connect(
                mUserModel.getId());
    }

    /**
     * sets the up the menu bar
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * Sets all of the items in the menu to do certain actions when clicked.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_contact) {
            navigateToAddUser();
            return true;
        }
        if (id == R.id.Pending_contact) {
            navigateToPendingContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * navigates to add user fragment
     */
    public void navigateToAddUser() {
        Navigation.findNavController(getView()).navigate(ContactsFragmentDirections.actionNavigationContactsToContactFragment());
    }

    /**
     * navigates to pending user fragment
     */
    public void navigateToPendingContact() {
        Navigation.findNavController(getView()).navigate(ContactsFragmentDirections.actionNavigationContactsToPendingContacts());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to FetchContactsViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {

        if (response.length() > 0) {
            if (response.has("code")) {

            } else {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("results"));
                    JSONObject jsonObject = null;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        firstName.add(jsonObject.getString("firstname"));
                        lastName.add(jsonObject.getString("lastname"));
                        userName.add(jsonObject.getString("username"));
                        memberId_b.add(jsonObject.getInt("memberid"));
                        primaryKey.add(jsonObject.getInt("primarykey"));
                        Log.e("display", String.valueOf(primaryKey.get(i)));
                        myContacts.add(new ContactsPost.Builder(userName.get(i), firstName.get(i), primaryKey.get(i), lastName.get(i))
                                .build());

                    }
                    postCount = firstName.size();
                    myAdapter = new ContactsRecyclerViewAdapter(myContacts);
                    myView.setAdapter(
                            myAdapter);
                    postCount = 0;
                    firstName = new ArrayList<>();
                    lastName = new ArrayList<>();
                    userName = new ArrayList<>();
                    memberId_b = new ArrayList<>();
                    myContacts = new ArrayList<>();
                    primaryKey = new ArrayList<>();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {

            Log.d("JSON Response", "No Response");
        }
    }
}