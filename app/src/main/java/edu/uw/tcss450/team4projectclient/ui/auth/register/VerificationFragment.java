package edu.uw.tcss450.team4projectclient.ui.auth.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentVerificationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerificationFragment extends Fragment {

    private FragmentVerificationBinding binding;

    private RegisterViewModel mRegisterModel;

    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentVerificationBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.binding.verificationButton.setOnClickListener(this::verify);
    }

    public final void verify(final View button) {
        VerificationFragmentArgs args = VerificationFragmentArgs.fromBundle(getArguments());
        if(binding.verificationField.getText().toString().equals(args.getVerificationCode())) {
            VerificationFragmentDirections.ActionVerificationFragmentToSignInFragment directions =
                    VerificationFragmentDirections.actionVerificationFragmentToSignInFragment();
            directions.setEmail(args.getEmail());
            directions.setPassword(args.getPassword());
            Navigation.findNavController(getView()).navigate(directions);
        } else {
            binding.verificationField.setError("Invalid code");
        }
    }
}
