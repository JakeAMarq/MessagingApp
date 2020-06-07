package edu.uw.tcss450.team4projectclient.ui.chatrooms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.ui.chatrooms.viewmodels.AddRemoveUsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditChatRoomDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditChatRoomDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHAT_ID = "chatId";

    // TODO: Rename and change types of parameters
    private AddRemoveUsersViewModel mAddRemoveUsersModel;

    private int mChatId;



    public EditChatRoomDialogFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param chatId
     * @return A new instance of fragment EditChatRoomDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditChatRoomDialogFragment newInstance(final int chatId) {
        EditChatRoomDialogFragment fragment = new EditChatRoomDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddRemoveUsersModel = new ViewModelProvider(getActivity()).get(AddRemoveUsersViewModel.class);
        if (getArguments() != null) {
            mChatId = getArguments().getInt(CHAT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_chat_room_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
