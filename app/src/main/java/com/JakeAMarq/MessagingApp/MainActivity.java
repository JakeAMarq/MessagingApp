package com.JakeAMarq.MessagingApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.JakeAMarq.MessagingApp.R;
import com.JakeAMarq.MessagingApp.databinding.ActivityMainBinding;
import com.JakeAMarq.MessagingApp.model.NewMessageCountViewModel;
import com.JakeAMarq.MessagingApp.services.PushReceiver;
import com.JakeAMarq.MessagingApp.ui.chat.ChatFragmentArgs;
import com.JakeAMarq.MessagingApp.ui.chat.ChatMessage;
import com.JakeAMarq.MessagingApp.ui.chat.MessageViewModel;
import com.JakeAMarq.MessagingApp.ui.chatrooms.ChatRoom;
import com.JakeAMarq.MessagingApp.ui.chatrooms.viewmodels.GetChatsViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.JakeAMarq.MessagingApp.model.UserInfoViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity containing NavHostFragment for res/navigation/main_graph and bottom navigation
 * with res/menu/menu_bottom_nav
 */
public class MainActivity extends AppCompatActivity {

    private MainPushMessageReceiver mPushMessageReceiver;

    private NewMessageCountViewModel mNewMessageModel;

    private GetChatsViewModel mChatRoomModel;

    private UserInfoViewModel mUserInfoModel;

    AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding binding;

    /**
     * Represents chatId of the chat room the user is currently in, 0 if user is not in a chat room
     */
    private int mCurrentChatId;

    /**
     * HashMap representing the number of unread messages per chatroom
     * Keys - chatIds
     * Values - number of unread messages in respective chatroom
     */
    private  Map<Integer, Integer> mUnreadMessageCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        mCurrentChatId = 0;
        mUnreadMessageCounts = new HashMap<>();

        ViewModelProvider provider = new ViewModelProvider(this);

        mUserInfoModel = new ViewModelProvider(
                this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt()))
                .get(UserInfoViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_chat_room_list,
                R.id.navigation_contacts,
                R.id.navigation_weather)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mNewMessageModel = provider.get(NewMessageCountViewModel.class);
        mChatRoomModel = provider.get(GetChatsViewModel.class);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chat_room) {
                mCurrentChatId = ChatFragmentArgs.fromBundle(arguments).getChatRoom().getId();
                //When the user navigates to a chat room, decrease the new message count by however
                //many unread message are in that chat room.
                if (mUnreadMessageCounts.containsKey(mCurrentChatId)) {
                    mNewMessageModel.subtract(mUnreadMessageCounts.get(mCurrentChatId));
                    mUnreadMessageCounts.put(mCurrentChatId, 0);
                }
            } else {
                mCurrentChatId = 0;
            }
        });

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chat_room_list);
            badge.setMaxCharacterCount(2);
            if (count > 0) {
                //new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter msgFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        IntentFilter chatFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_CHAT);
        registerReceiver(mPushMessageReceiver, msgFilter);
        registerReceiver(mPushMessageReceiver, chatFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    /**
     * Sets title of action bar
     * @param title new title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public boolean hasUnreadMessages(final int chatId) {
        return mUnreadMessageCounts.containsKey(chatId) && mUnreadMessageCounts.get(chatId) > 0;
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        private MessageViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(MessageViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();
            if (intent.hasExtra("chatMessage")) {

                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");
                int chatId = intent.getIntExtra("chatId", 0);

                //If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (mCurrentChatId != chatId) {
                    if (mUnreadMessageCounts.containsKey(chatId)) {
                        mUnreadMessageCounts.put(chatId, mUnreadMessageCounts.get(chatId) + 1);
                    } else {
                        mUnreadMessageCounts.put(chatId, 1);
                    }

                    mNewMessageModel.increment();
                }
                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(intent.getIntExtra("chatId", -1), cm);
            } else if (intent.hasExtra("chatRoom")) {

                ChatRoom chatRoom = (ChatRoom) intent.getSerializableExtra("chatRoomObject");
                int chatId = intent.getIntExtra("chatId", 0);

                mChatRoomModel.getChatRooms(mUserInfoModel.getJwt());
                Toast.makeText(MainActivity.this, "You've been added to chat room: " + chatRoom.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
