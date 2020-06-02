package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingContacts extends Fragment {

    private FetchPendingContactsViewModel mFetchViewModel;
    private ArrayList<String> firstName, lastName, userName;
    private ArrayList<Integer> memberId_b, primaryKey, memberId_a;
    private RecyclerView myView;
    private List<PendingContactsInfo> myContacts;
    public static AcceptUserViewModel mAcceptContact;

    private PendingContactsRecyclerViewAdapter myAdapter;

    public PendingContacts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFetchViewModel = new ViewModelProvider(getActivity()).get(FetchPendingContactsViewModel.class);
        firstName = new ArrayList<>();
        lastName = new ArrayList<>();
        userName = new ArrayList<>();
        memberId_b = new ArrayList<>();
        memberId_a = new ArrayList<>();
        myContacts = new ArrayList<>();
        primaryKey = new ArrayList<>();
        mAcceptContact = new ViewModelProvider(getActivity()).get(AcceptUserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_contacts, container, false);
        if (view instanceof RecyclerView) {
            myView = (RecyclerView) view;
//            myView.setAdapter(
//                    new ContactsRecyclerViewAdapter(ContactsGenerator.getBlogList()));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);
        mAcceptContact.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponseAccept);
        verifyAuthWithServer();
    }

    private void verifyAuthWithServer() {
        mFetchViewModel.connect(
                SignInFragment.memberId_a);
    }

    private void observeResponseAccept(final JSONObject response) {
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
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to FetchContactsViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        Log.e("poooooo", String.valueOf(response));

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
                        memberId_a.add(jsonObject.getInt("memberid_b"));
                        primaryKey.add(jsonObject.getInt("primarykey"));

                        Log.e("display", String.valueOf(primaryKey.get(i)));
                        myContacts.add(new PendingContactsInfo.Builder(userName.get(i), firstName.get(i), primaryKey.get(i), lastName.get(i),
                                memberId_a.get(i), memberId_b.get(i))
                                .build());

                    }
                    myAdapter = new PendingContactsRecyclerViewAdapter(myContacts);
                    myView.setAdapter(
                            myAdapter);

                    firstName = new ArrayList<>();
                    lastName = new ArrayList<>();
                    userName = new ArrayList<>();
                    memberId_b = new ArrayList<>();
                    memberId_a = new ArrayList<>();
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
