package edu.uw.tcss450.team4projectclient.ui.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements Serializable {

    private final int mChatId;
    private final List<ChatMessage> mMessages;

    public ChatRoom(int chatId) {
        mChatId = chatId;
        mMessages = new ArrayList<>();
    }

    public ChatRoom(int chatId, List<ChatMessage> messages) {
        mChatId = chatId;
        mMessages = messages;
    }

    public int getChatId() {
        return mChatId;
    }

    public List<ChatMessage> getMessages() {
        return mMessages;
    }

    public String getLastMessage() {
        return mMessages.size() > 0 ? mMessages.get(mMessages.size() - 1).getMessage() : "";
    }

    public String getLastTimeStamp() {
        return mMessages.size() > 0 ? mMessages.get(mMessages.size() - 1).getTimeStamp() : "";
    }

    public void addMessage(final ChatMessage message) {
        mMessages.add(message);
    }
}
