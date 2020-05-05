/**
 * Team 4
 * This class is the registration fragment.
 */

package edu.uw.tcss450.team4projectclient.ui.auth.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import edu.uw.tcss450.team4projectclient.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.team4projectclient.ui.auth.register.RegisterViewModel;
import edu.uw.tcss450.team4projectclient.utils.PasswordValidator;

import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    // Binding to have access to all of the components in the xml.
    private FragmentRegisterBinding binding;

    // This is the view model class to help with registration
    private RegisterViewModel mRegisterModel;

    // This field helps with verifying user inputs.
    private PasswordValidator mNameValidator = checkPwdLength(1);

    // This field helps with verifying user inputs.
    private PasswordValidator mEmailValidator = checkPwdLength()
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    // This field helps with verifying user inputs.
    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    // random six digit number
    private int randomCode = new Random().nextInt(900000) + 100000;

    public RegisterFragment() {
        // Required empty public constructor
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
     * Sets up the view for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentRegisterBinding.inflate(inflater);
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

        this.binding.buttonRegister.setOnClickListener(this::attemptRegister);
        // adds the observer to the class
        mRegisterModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );

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
                    binding.editEmail.setError(
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
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * Upon clicking register button, it checks the different fields to validate user.
     * @param button
     */
    private void attemptRegister(final View button) {
        validateFirst();
    }

    /**
     * validate User's input for first name
     */
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editFirst.getText().toString().trim()),
                this::validateLast,
                result -> binding.editFirst.setError("Please enter a valid first name."));
    }

    /**
     * validate User's input for last name
     */
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editLast.getText().toString().trim()),
                this::validateEmail,
                result -> binding.editLast.setError("Please enter a valid last name."));
    }

    /**
     * validate User's input for email
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * validate User's input for a valid password.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword1.getText().toString()),
                this::validateNickName,
                result -> binding.editPassword1.setError("Please enter a valid Password."));
    }

    /**
     * Checks if user has a valid nickname.
     */
    private void validateNickName() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.nickName.getText().toString().trim()),
                this::verifyAuthWithServer,
                result -> binding.editLast.setError("Please enter a valid last name."));
    }

    /**
     * Sends all of the required information to the server to register the user.
     */
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                binding.editFirst.getText().toString(),
                binding.editLast.getText().toString(),
                binding.editEmail.getText().toString(),
                binding.editPassword1.getText().toString(),
                binding.nickName.getText().toString(),
                Integer.toString(randomCode));
    }

    /**
     * Upon successful registration, the user navigates to the registration page.
     */
    private void navigateToLogin() {
        RegisterFragmentDirections.ActionRegisterFragmentToVerificationFragment directions =
                RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment(binding.editEmail.getText().toString(),
                        binding.editPassword1.getText().toString(),
                        Integer.toString(randomCode));
        directions.setEmail(binding.editEmail.getText().toString());
        directions.setPassword(binding.editPassword1.getText().toString());
        directions.setVerificationCode(Integer.toString(randomCode));
        Navigation.findNavController(getView()).navigate(directions);
    }
}
