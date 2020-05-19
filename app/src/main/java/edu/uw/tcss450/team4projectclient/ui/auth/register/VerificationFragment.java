/**
 * Team 4
 * This class waits for the user to verify their email before registering them.
 */
package edu.uw.tcss450.team4projectclient.ui.auth.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentVerificationBinding;
import edu.uw.tcss450.team4projectclient.ui.auth.register.RegisterViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerificationFragment extends Fragment {

    /**
     * binding to get access to the UI elements.
     */
    private FragmentVerificationBinding binding;

    /**
     * the code that the user needs to match.
     */
    private String verificationCode;

    /**
     * arguments.
     */
    private VerificationFragmentArgs args;

    /**
     *
     */
    private RegisterViewModel mRegisterModel;

    public VerificationFragment() {
        // Required empty public constructor
    }

    /**
     * onCreate that gets called when the class is initialized
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
        args = VerificationFragmentArgs.fromBundle(getArguments());
    }

    /**
     * Sets up the view for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentVerificationBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return this.binding.getRoot();
    }

    /**
     * Gets called when the view gets created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRegisterModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );

        // send a verification code to this email
        mRegisterModel.verify(args.getEmail(), "false");

        /**
         * when the user clicks the verify button, it checks their code if they have entered the correct code.
         */
        this.binding.buttonVerify.setOnClickListener(this::verify);
        // disabling until the code is retrieved
        this.binding.buttonVerify.setActivated(false);
        this.binding.buttonVerify.setClickable(false);

    }


    /**
     * This validates the response from the server and checks if it's valid, then it navigates to login
     *  if successful
     * @param response the JSONObject response
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editVerificationCode.setError(
                            "Error Authenticating: " +
                                    new JSONObject(
                                            response.getString("data").
                                                    replace("'", "\"")
                                    ).getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                // navigate to login to login the user.
                try {
                    verificationCode = response.getString("verification");
                    this.binding.buttonVerify.setActivated(true);
                    this.binding.buttonVerify.setClickable(true);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
    /**
     * Verify's the user's email address and registers them if they entered the correct code.
     * @param button
     */
    public final void verify(final View button) {

        /**
         * checks the user's input if it matches the sent verification code.
         */
        if(binding.editVerificationCode.getText().toString().equals(verificationCode)) {
            mRegisterModel.verify(args.getEmail(), "true");
            VerificationFragmentDirections.ActionVerificationFragmentToSignInFragment directions =
                    VerificationFragmentDirections.actionVerificationFragmentToSignInFragment();
            directions.setEmail(args.getEmail());
            directions.setPassword(args.getPassword());
            Navigation.findNavController(getView()).navigate(directions);


             // if code doesn't match the sent code the registration doesn't go through
        } else {
            binding.editVerificationCode.setError("Invalid code");
        }
    }
}
