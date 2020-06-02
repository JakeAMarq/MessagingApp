

package edu.uw.tcss450.team4projectclient.ui.contacts;

import java.io.Serializable;

public class PendingContactsInfo implements Serializable {
    private final String mfName;
    private final String mlName;
    private final String muserName;
    private final Integer wantedPrimaryA;
    private final Integer wantedPrimaryB;
    private final Integer mprimaryKey;

    /**
     * class to build the Recycler view
     */
    public static class Builder {
        private final String mfName;
        private final String mlName;
        private final String muserName;
        private Integer mprimaryKey;
        private final Integer wantedPrimaryA;
        private final Integer wantedPrimaryB;


        /**
         * Constructs a new Builder.
         *
         * @param username the User's userName
         * @param title the user's first name
         */
        public Builder(String username, String title, Integer primaryKey, String lastName, Integer wantedPrimaryA,
            Integer wantedPrimaryB) {
            this.muserName = username;
            this.mfName = title;
            this.mprimaryKey = primaryKey;
            this.mlName = lastName;
            this.wantedPrimaryA = wantedPrimaryA;
            this.wantedPrimaryB = wantedPrimaryB;
        }


        public PendingContactsInfo build() {
            return new PendingContactsInfo(this);
        }

    }

    private PendingContactsInfo(final Builder builder) {
        this.mprimaryKey = builder.mprimaryKey;

        this.mfName = builder.mfName;
        this.mlName = builder.mlName;
        this.muserName = builder.muserName;
        this.wantedPrimaryA = builder.wantedPrimaryA;
        this.wantedPrimaryB = builder.wantedPrimaryB;
    }


    public Integer getMprimaryKey() {
        return mprimaryKey;
    }
    public Integer getWantedPrimaryA() {
        return wantedPrimaryA;
    }
    public Integer getWantedPrimaryB() {
        return wantedPrimaryB;
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

