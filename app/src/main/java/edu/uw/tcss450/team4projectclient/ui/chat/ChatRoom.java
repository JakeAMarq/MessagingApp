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
    private final int mChatId;

    /**
     * Messages in chat room
     */
    private final List<ChatMessage> mMessages;


    /**
     * Creates an instance of ChatRoom with no messages in it
     * @param chatId chatId of chat room
     */
    public ChatRoom(int chatId) {
        mChatId = chatId;
        mMessages = new ArrayList<>();
    }

    /**
     * Creates an instance of ChatRoom with a list of messages in it
     * @param chatId chatId of chat room
     * @param messages list of messages in chat room
     */
    public ChatRoom(int chatId, List<ChatMessage> messages) {
        mChatId = chatId;
        mMessages = messages;
    }

    /**
     * Returns chatId of chat room
     * @return chatId of chat room
     */
    public int getChatId() {
        return mChatId;
    }

    /**
     * Returns list of the messages in the chat room
     * @return list of the messages in the chat room
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
     * Adds message to chat room
     * @param message the message
     */
    public void addMessage(final ChatMessage message) {
        mMessages.add(message);
    }
}
