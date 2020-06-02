package edu.uw.tcss450.team4projectclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.team4projectclient.databinding.ActivityMainBinding;
import edu.uw.tcss450.team4projectclient.model.NewMessageCountViewModel;
import edu.uw.tcss450.team4projectclient.model.UserInfoViewModel;
import edu.uw.tcss450.team4projectclient.services.PushReceiver;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatFragmentArgs;
import edu.uw.tcss450.team4projectclient.ui.chat.ChatMessage;
import edu.uw.tcss450.team4projectclient.ui.chatrooms.ChatRoom;
import edu.uw.tcss450.team4projectclient.ui.chat.MessageViewModel;
import edu.uw.tcss450.team4projectclient.ui.chatrooms.viewmodels.GetChatsViewModel;
import edu.uw.tcss450.team4projectclient.ui.contacts.AddContactsViewModel;
import edu.uw.tcss450.team4projectclient.ui.contacts.ContactsPost;

/**
 * Activity containing NavHostFragment for res/navigation/main_graph and bottom navigation
 * with res/menu/bottom_nav_menu
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Viewbinding of activity
     */
    private ActivityMainBinding binding;

    /**
     * chatId of the chat room the user is currently viewing, 0 if user is not viewing a chat room
     */
    private int mCurrentChatId;

    /**
     * Broadcast receiver used to handle receiving Intents from PushReceiver
     */
    private MainPushMessageReceiver mPushMessageReceiver;

    /**
     * ViewModel used to store number of unread messages
     */
    private NewMessageCountViewModel mNewMessageModel;

    /**
     * ViewModel used to retrieve chat rooms the user is in
     */
    private GetChatsViewModel mGetChatsModel;

   // private AddContactsViewModel mAddContacts;

    /**
     * ViewModel used to store user's email and JWT
     */
    private UserInfoViewModel mUserInfoModel;

    /**
     * The AppBarConfiguration
     */
    AppBarConfiguration mAppBarConfiguration;

    /**
     * HashMap representing the number of unread messages per chat room
     * Keys - chatIds
     * Values - number of unread messages in respective chat room
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
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt(), args.getMemberId()))
                .get(UserInfoViewModel.class);
        mGetChatsModel = provider.get(GetChatsViewModel.class);
        mNewMessageModel = provider.get(NewMessageCountViewModel.class);
       // mAddContacts = provider.get(AddContactsViewModel.class);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_chat_room_list,
                R.id.navigation_contacts,
                R.id.navigation_weather)
                .build();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
        IntentFilter contactFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_CONTACT);
        registerReceiver(mPushMessageReceiver, msgFilter);
        registerReceiver(mPushMessageReceiver, chatFilter);
        registerReceiver(mPushMessageReceiver, contactFilter);
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

    /**
     * Returns true if chat room has unread messages, false otherwise
     * @param chatId ID of chat room
     * @return true if chat room has unread messages, false otherwise
     */
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

                mGetChatsModel.getChatRooms(mUserInfoModel.getJwt());
                Toast.makeText(MainActivity.this, "You've been added to chat room: " + chatRoom.getName(), Toast.LENGTH_LONG).show();
            } else if (intent.hasExtra("contactsPost")) {
                ContactsPost contactsPost = (ContactsPost) intent.getSerializableExtra("contactsPost");
                Log.e("I AM HEREEEEE", "YOOOOOOOOOOO");
            }
        }
    }

}
