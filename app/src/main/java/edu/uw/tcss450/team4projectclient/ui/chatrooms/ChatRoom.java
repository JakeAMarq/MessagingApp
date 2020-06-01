package edu.uw.tcss450.team4projectclient.ui.chatrooms;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.team4projectclient.ui.chat.ChatMessage;

/**
 * Class representing a chat room
 */
public class ChatRoom implements Serializable {

    /**
     * ID of chat room
     */
    private final int mId;

    /**
     * Name of chat room
     */
    private final String mName;

    /**
     * Owner of chat room's email
     */
    private final String mOwner;

    /**
     * Messages in chat room
     */
    private List<ChatMessage> mMessages;

    /**
     * Creates an instance of ChatRoom with no messages in it
     * @param chatId chatId of chat room
     */
    public ChatRoom(final int chatId, final String name, final String owner) {
        mId = chatId;
        mName = name;
        mOwner = owner;
        mMessages = new ArrayList<>();
    }

    /**
     * Creates an instance of ChatRoom with a list of messages in it
     * @param chatId chatId of chat room
     * @param messages list of messages in chat room
     */
    public ChatRoom(final int chatId, final String name, final String owner, final List<ChatMessage> messages) {
        mId = chatId;
        mName = name;
        mOwner = owner;
        mMessages = messages;
    }

    /**
     * Returns an instance of ChatRoom created from the JSON string
     * @param crAsJson the JSON string
     * @return an instance of ChatRoom created from the JSON string
     * @throws JSONException
     */
    public static ChatRoom createFromJsonString(final String crAsJson) throws JSONException {
        final JSONObject chatRoom = new JSONObject(crAsJson);
        return new ChatRoom(chatRoom.getInt("chatId"),
                chatRoom.getString("chatName"),
                chatRoom.getString("owner"));
    }

    /**
     * Returns the id of the chat room
     * @return the id of the chat room
     */
    public int getId() {
        return mId;
    }

    /**
     * Returns the name of the chat room
     * @return the name of the chat room
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the owner of the chat room's email
     * @return the owner of the chat room's email
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * Returns the list of messages in the chat room
     * @return the list of messages in the chat room
     */
    public List<ChatMessage> getMessages() {
        return mMessages;
    }

    /**
     * Returns content of most recent message if messages exist in the chat room or an empty
     * string otherwise
     * @return content of most recent message if messages exist in the chat room or an empty
     * string otherwise
     */
    public String getLastMessage() {
        return mMessages.size() > 0 ? mMessages.get(mMessages.size() - 1).getMessage() : "";
    }

    /**
     * Returns timestamp of most recent message if messages exist in the chat room or an empty
     * string otherwise
     * @return timestamp of most recent message if messages exist in the chat room or an empty
     * string otherwise
     */
    public String getLastTimeStamp() {
        return mMessages.size() > 0 ? mMessages.get(mMessages.size() - 1).getTimeStamp() : "";
    }

    /**
     * Sets the chat room's messages
     * @param messages the messages
     */
    public void setMessages(List<ChatMessage> messages) {
        mMessages = messages;
    }
}
