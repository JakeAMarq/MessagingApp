package com.JakeAMarq.MessagingApp.ui.contacts;

import java.io.Serializable;

public class Contact implements Serializable {

    private String mFirst;
    private String mLast;
    private String mUser;
    private String mEmail;

    public Contact(String first, String last, String user, String email) {
        mFirst = first;
        mLast = last;
        mUser = user;
        mEmail = email;
    }

    public String getFirst() {
        return mFirst;
    }

    public String getLast() {
        return mLast;
    }

    public String getUser() {
        return mUser;
    }

    public String getEmail() {
        return mEmail;
    }
}
