package edu.uw.tcss450.team4projectclient.ui.contacts;

import java.io.Serializable;

public class ContactsPost implements Serializable {

    private final String mfName;
    private final String mlName;
    private final String muserName;
    private final Integer mprimaryKey;

    /**
     * class to build the Recycler view
     */
    public static class Builder {
        private final String mfName;
        private final String mlName;
        private final String muserName;
        private Integer mprimaryKey;



        /**
         * Constructs a new Builder.
         *
         * @param username the User's userName
         * @param firstname the user's first name
         */
        public Builder(String username, String firstname, Integer primaryKey, String lastName) {
            this.muserName = username;
            this.mfName = firstname;
            this.mprimaryKey = primaryKey;
            this.mlName = lastName;
        }

        public ContactsPost build() {
            return new ContactsPost(this);
        }

    }

    private ContactsPost(final Builder builder) {
        this.mprimaryKey = builder.mprimaryKey;

        this.mfName = builder.mfName;
        this.mlName = builder.mlName;
        this.muserName = builder.muserName;
    }

    public Integer getMprimaryKey() {
        return mprimaryKey;
    }

    public String getLastName() {
        return mlName;
    }

    public String getFName() {
        return mfName;
    }
    public String getLName() {
        return mlName;
    }
    public String getUserName() {
        return muserName;
    }


}
