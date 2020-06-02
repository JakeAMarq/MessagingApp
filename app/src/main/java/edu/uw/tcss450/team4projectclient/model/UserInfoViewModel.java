package edu.uw.tcss450.team4projectclient.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * ViewModel containing user's email and JWT
 */
public class UserInfoViewModel extends ViewModel {

    private final String mEmail;
    private final String mJwt;
    private final Integer mMember_id;

    private UserInfoViewModel(String email, String jwt, Integer member_id) {
        mEmail = email;
        mJwt = jwt;
        mMember_id = member_id;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getJwt() {
        return mJwt;
    }

    public Integer getId() {
        return mMember_id;
    }

    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final String email;
        private final String jwt;
        private final Integer member_id;

        public UserInfoViewModelFactory(String email, String jwt, Integer member_id) {
            this.email = email;
            this.jwt = jwt;
            this.member_id = member_id;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, jwt, member_id);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }
}
