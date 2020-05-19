package edu.uw.tcss450.team4projectclient.ui.contacts;

import java.io.Serializable;

public class ContactsPost implements Serializable {

    private final String mPubDate;
    private final String mTitle;
    private final String mUrl;
    private final String mTeaser;
    private final String mAuthor;
    private final String mfName;
    private final String mlName;
    private final String muserName;

    private final Integer mprimaryKey;

    /**
     * class to build the Recycler view
     */
    public static class Builder {
        private final String mPubDate;
        private final String mTitle;
        private  String mUrl = "";
        private  String mTeaser = "";
        private  String mAuthor = "";
        private String mfName = "";
        private String mlName = "";
        private String muserName = "";
        private Integer mprimaryKey;


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the User's userName
         * @param title the user's first name
         */
        public Builder(String pubDate, String title, Integer primaryKey, String lastName) {
            this.mPubDate = pubDate;
            this.mTitle = title;
            this.mfName = title;
            this.mprimaryKey = primaryKey;
            this.mlName = lastName;
        }

        /**
         * Add an optional url for the full blog post.
         * @param val an optional url for the full blog post
         * @return the Builder of this BlogPost
         */
        public Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this BlogPost
         */
        public Builder addTeaser(final String val) {
            mTeaser = val;
            return this;
        }
        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this BlogPost
         */
        public Builder firstName(final String val) {
            mfName = val;
            return this;
        }
        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this BlogPost
         */
        public Builder lastName(final String val) {
            mlName = val;
            return this;
        }


        public ContactsPost build() {
            return new ContactsPost(this);
        }

    }

    private ContactsPost(final Builder builder) {
        this.mPubDate = builder.mPubDate;
        this.mprimaryKey = builder.mprimaryKey;
        this.mTitle = builder.mTitle;
        this.mUrl = builder.mUrl;
        this.mTeaser = builder.mTeaser;
        this.mAuthor = builder.mAuthor;
        this.mfName = builder.mfName;
        this.mlName = builder.mlName;
        this.muserName = builder.muserName;
    }

    public String getPubDate() {
        return mPubDate;
    }
    public Integer getMprimaryKey() {
        return mprimaryKey;
    }

    public String getTitle() {
        return mTitle;
    }
    public String getLastName() {
        return mlName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTeaser() {
        return mTeaser;
    }

    public String getAuthor() {
        return mAuthor;
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
