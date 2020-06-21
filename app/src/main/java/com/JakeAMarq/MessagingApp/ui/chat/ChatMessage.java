package com.JakeAMarq.MessagingApp.ui.chat;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Encapsulate chat message details.
 */
public final class ChatMessage implements Serializable {

    /**
     * ID of the message
     */
    private final int mMessageId;

    /**
     * The actual message
     */
    private final String mMessage;

    /**
     * Sender of the message's email
     */
    private final String mSender;

    /**
     * Timestamp from when the message was sent
     */
    private final String mTimeStamp;

    /**
     * Creates new instance of ChatMessage
     * @param messageId ID of the message
     * @param message the message itself
     * @param sender Sender of the message's email
     * @param timeStamp Timestamp of when message was sent
     */
    public ChatMessage(int messageId, String message, String sender, String timeStamp) {
        mMessageId = messageId;
        mMessage = message;
        mSender = sender;
        mTimeStamp = timeStamp;
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * ChatMessage object.
     * @param cmAsJson the String to be parsed into a ChatMessage Object.
     * @return a ChatMessage Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
     */
    public static ChatMessage createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject msg = new JSONObject(cmAsJson);
        return new ChatMessage(msg.getInt("messageid"),
                msg.getString("message"),
                msg.getString("email"),
                msg.getString("timestamp"));
    }

    /**
     * Returns the message
     * @return the message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Returns the sender's email
     * @return the sender's email
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Returns the timestamp from when the message was sent
     * @return the timestamp from when the message was sent
     */
    public String getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * Returns the message's ID
     * @return the message's ID
     */
    public int getMessageId() {
        return mMessageId;
    }

    /**
     * Provides equality solely based on MessageId.
     * @param other the other object to check for equality
     * @return true if other message ID matches this message ID, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof ChatMessage) {
            result = mMessageId == ((ChatMessage) other).mMessageId;
        }
        return result;
    }
}

