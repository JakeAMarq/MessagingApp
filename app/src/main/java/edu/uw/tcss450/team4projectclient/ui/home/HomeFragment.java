package edu.uw.tcss450.team4projectclient.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import edu.uw.tcss450.team4projectclient.R;

/**
 * Home page
 */
public class HomeFragment extends Fragment {

    /**
     * Required empty public constructor
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.OverlayThemeFall);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.fragment_home, container, false);


//        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    /**
     * sets the up the menu bar
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    /**
     * Sets all of the items in the menu to do certain actions when clicked.
     * @param item
     *return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.change_color) {
            //TODO open a settings fragment
            startActivity(new Intent(getContext(),Home_Color.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
