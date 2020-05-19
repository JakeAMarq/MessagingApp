package edu.uw.tcss450.team4projectclient.ui.auth;

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
import edu.uw.tcss450.team4projectclient.databinding.FragmentResetPasswordBinding;
import edu.uw.tcss450.team4projectclient.ui.auth.register.RegisterViewModel;
import edu.uw.tcss450.team4projectclient.utils.PasswordValidator;

import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.checkPwdUpperCase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;

    private RegisterViewModel mRegisterModel;

    private String verificationCode;

    private String chosenEmail;

    private String chosenPassword;

    // This field helps with verifying user inputs.
    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentResetPasswordBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return this.binding.getRoot();
    }

    /**
     * onCreate that gets called when the class is initialized
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting the view model
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    /**
     * Gets called when the view gets created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.binding.buttonSend.setOnClickListener(this::sendCode);
        this.binding.buttonReset.setOnClickListener(this::validatePassword);
        binding.buttonReset.setActivated(false);
        binding.buttonReset.setClickable(false);
        // adds the observer to the class
        mRegisterModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );

    }

    /**
     * The user has successfully reset their password.
     */
    private void navigateToLogin() {

    }

    private void sendCode(final View button) {
        if(binding.editEmail.getText().toString().contains("@")) {
            // storing the email used when the user pushed the button
            // so that changing the field later won't break things.
            chosenEmail = binding.editEmail.getText().toString();
            mRegisterModel.verifyReset(chosenEmail);
        } else {
            binding.editEmail.setError("Please enter an email address.");
        }
    }

    /**
     * validate User's input for a valid password.
     */
    private void validatePassword(final View button) {
        chosenPassword = binding.editPassword.getText().toString();
        if(chosenPassword.equals(binding.editPassword2.getText().toString())) {
            mPassWordValidator.processResult(
                    mPassWordValidator.apply(chosenPassword),
                    this::resetPassword,
                    result -> binding.editPassword.setError("Please enter a valid password."));
        } else {
            binding.editPassword2.setError("Passwords do not match");
        }

    }

    private void resetPassword() {
        // user entered the correct code.
        if(binding.editVerification.getText().toString().equals(verificationCode)) {
            mRegisterModel.resetPassword(chosenEmail, chosenPassword);
            ResetPasswordFragmentDirections.ActionResetPasswordFragmentToSignInFragment directions =
                    ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment();
            directions.setEmail(chosenEmail);
            directions.setPassword(chosenPassword);
            Navigation.findNavController(getView()).navigate(directions);
        } else {
            binding.editVerification.setError("Incorrect code"); // my kind of code
        }
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
                    binding.editVerification.setError(
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
                    if(response.has("verification")) {
                        verificationCode = response.getString("verification");
                        this.binding.buttonReset.setActivated(true);
                        this.binding.buttonReset.setClickable(true);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
