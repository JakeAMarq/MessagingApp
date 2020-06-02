package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentContactsPostBinding;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsPostFragment extends Fragment {

    private DeleteContactsViewModel mDeleteContactsViewModel;
    ContactsPostFragmentArgs args;

    public ContactsPostFragment() {
        // Required empty public constructor
    }

    /**
     * oncreate method initializes the view models
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeleteContactsViewModel = new ViewModelProvider(getActivity()).get(DeleteContactsViewModel.class);
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
        return inflater.inflate(R.layout.fragment_contacts_post, container, false);
    }

    /**
     * Sets all of the buttons onClick listeners
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeleteContactsViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);
        args = ContactsPostFragmentArgs.fromBundle(getArguments());
        FragmentContactsPostBinding binding = FragmentContactsPostBinding.bind(getView());
        binding.textPubdate.setText(args.getContact().getUserName());
        binding.textTitle.setText(args.getContact().getFName());
        final String preview = "";
        binding.textPreview.setText(preview);
        //Note we are using an Intent here to start the default system web browser
        binding.buttonUrl.setOnClickListener(button -> verifyAuthWithServer());
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(args.getContact().getUrl()))));
    }

    /**
     * connects to my backend for deleting a contact
     */
    private void verifyAuthWithServer() {
    //Log.e("priiiiiii", String.valueOf(args.getContact().getMprimaryKey()));
            mDeleteContactsViewModel.connect(
                    args.getContact().getMprimaryKey()
            );
    }

    /**
     * This validates the response from the server and checks if it's valid, then it connects the user
     *  if successful
     * @param response the JSONObject response
     */
    private void observeResponse(final JSONObject response) {

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

                // navigate to Contacts to login the user.
                Toast.makeText(getContext(), "You've Deleted " + args.getContact().getUserName(), Toast.LENGTH_LONG).show();
               // getActivity().getSupportFragmentManager().popBackStackImmediate();
                Navigation.findNavController(getView()).navigate(ContactsPostFragmentDirections.actionContactsPostFragmentToNavigationContacts3());
                //Navigation.findNavController(getView()).navigate(ContactFragmentDirections.actionContactFragmentToNavigationContacts());
            }
        } else {
            Log.e("JSON Response", "No Response");
        }
    }
}
