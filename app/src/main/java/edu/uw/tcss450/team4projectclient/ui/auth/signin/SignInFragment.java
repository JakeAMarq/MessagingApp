package edu.uw.tcss450.team4projectclient.ui.auth.signin;

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

import edu.uw.tcss450.team4projectclient.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team4projectclient.model.PushyTokenViewModel;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.utils.PasswordValidator;
import static edu.uw.tcss450.team4projectclient.utils.PasswordValidator.*;

/**
 * Team 4
 * This class helps a user log into their account.
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private PushyTokenViewModel mPushyTokenViewModel;
    private UserInfoViewModel mUserViewModel;

    // Binding to have access to all of the components in the xml.
    private FragmentSignInBinding binding;
    // This is the view model class to help with signing in
    private SignInViewModel mSignInModel;
    // This field helps with verifying user email input.
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));
    // This field helps with verifying user password input.
    private PasswordValidator mPassWordValidator = checkPwdLength(1)
            .and(checkExcludeWhiteSpace());

    public static int memberId_a;
    public SignInFragment() {
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
        mSignInModel = new ViewModelProvider(getActivity()).get(SignInViewModel.class);

        mPushyTokenViewModel = new ViewModelProvider(getActivity()).get(PushyTokenViewModel.class);
    }
    /**
     * Sets up the view for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentSignInBinding.inflate(inflater, container, false);
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



        // adding action to login button
        this.binding.buttonLogIn.setOnClickListener(this::attemptLogin);
        this.binding.buttonRegister.setOnClickListener(button -> navigateToRegisterFragment());
        this.binding.buttonForgotPassword.setOnClickListener(this::navigateToResetPasswordFragment);
        // adds the observer to the class
        mSignInModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse
        );
        //making sure the email and password are not empty
        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        //TODO: Remove hard-coded email and password
        binding.editEmail.setText(args.getEmail().equals("default") ? "test1@test.com" : args.getEmail());//test1@test.com
        binding.editPassword.setText(args.getPassword().equals("default") ? "test12345" : args.getPassword());

        //don't allow sign in until pushy token retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                binding.buttonLogIn.setEnabled(!token.isEmpty()));

        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);
    }

    private void attemptLogin(final View button) {validateEmail();}
    /**
     * validate User's input for a valid email.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editEmail.setError("Please enter a valid email address."));
    }
    /**
     * validate User's input for a valid password.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword.setError("Please enter a valid password."));
    }
    /**
     * Sends all of the required information to the server to register the user.
     */
    private void verifyAuthWithServer() {
        mSignInModel.connect(
                binding.editEmail.getText().toString(),
                binding.editPassword.getText().toString());
    }
    /**
     * navigates to register fragment
     */
    private void navigateToRegisterFragment() {
        Navigation.findNavController(getView()).navigate(SignInFragmentDirections.actionSignInFragmentToRegisterFragment());
    }
    /**
     * Upon successful login the user navigates to the main page.
     * @param email
     * @param jwt
     */
    private void navigateToMainActivity(String email, String jwt) {
        SignInFragmentDirections.ActionSignInFragmentToMainActivity directions =
                SignInFragmentDirections.actionSignInFragmentToMainActivity(email, jwt);
        Navigation.findNavController(getView()).navigate(directions);
    }

    /**
     * Upon successful login, but without a verified email,
     * the user navigates to the verification page.
     * @param email
     * @param password
     */
    private void navigateToVerificationFragment(String email, String password) {
        SignInFragmentDirections.ActionSignInFragmentToVerificationFragment directions =
                SignInFragmentDirections.actionSignInFragmentToVerificationFragment(email, password);
        Navigation.findNavController(getView()).navigate(directions);
    }

    /**
     * When the user has forgotten their password
     * the user navigates to the reset password page.
     */
    private void navigateToResetPasswordFragment(final View button) {
        Navigation.findNavController(getView()).navigate(SignInFragmentDirections.actionSignInFragmentToResetPasswordFragment());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        Log.e("oooo", String.valueOf(response));
        if (response.length() > 0) {
            if (response.has("code")) {
                try {


                    binding.editEmail.setError(
                            "Error Authenticating: " +
                                    new JSONObject(response.getString("data").
                                            replace("'", "\""))
                                            .getString("message"));

                } catch (JSONException e) {
//                    Log.e("test", response.toString());
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    // Need to send unverified user to verification page
                    if(response.getString("verification").equals("1")) {

                        memberId_a = response.getInt("memberid");
                        mUserViewModel = new ViewModelProvider(getActivity(),
                                new UserInfoViewModel.UserInfoViewModelFactory(
                                        binding.editEmail.getText().toString(),
                                        response.getString("token")
                                )).get(UserInfoViewModel.class);
                        sendPushyToken();
                    } else {
                        navigateToVerificationFragment(
                                binding.editEmail.getText().toString(),
                                binding.editPassword.getText().toString());
                        mSignInModel.resetData();
                    }

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param response the Response from the server
     */
    private void observePushyPutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                binding.editEmail.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else {
                navigateToMainActivity(
                        binding.editEmail.getText().toString(),
                        mUserViewModel.getJwt()
                );
            }
        }
    }


    /**
     * Helper to abstract the request to send the pushy token to the web service
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getJwt());
    }

}
