package edu.uw.tcss450.team4projectclient.ui.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ChatRoom implements Serializable {

    /**
     * ChatId of chat room
     */
    private final int mId;

    private final String mName;

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

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getOwner() {
        return mOwner;
    }

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
     * Sets chatroom messages
     * @param messages the messages
     */
    public void setMessages(List<ChatMessage> messages) {
        mMessages = messages;
    }
}
