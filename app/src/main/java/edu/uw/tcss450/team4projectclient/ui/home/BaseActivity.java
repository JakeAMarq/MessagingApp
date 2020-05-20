package edu.uw.tcss450.team4projectclient.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import edu.uw.tcss450.team4projectclient.R;
public class BaseActivity extends AppCompatActivity {
    private final static int THEME_BANANA = 1;
    private final static int THEME_VIBRANT = 2;
    private final static int THEME_FALL = 3;
    private final static int THEME_DARK = 4;
    private final static int THEME_ICE = 5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }
    public void updateTheme() {
        if (Home_Color.getTheme(getApplicationContext()) <= THEME_BANANA) {
            setTheme(R.style.OverlayThemeBanana);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkColor));
            }
        } else if (Home_Color.getTheme(getApplicationContext()) == THEME_VIBRANT) {
            setTheme(R.style.OverlayThemeVibrant);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.vibrant_green));
            }
        } else if (Home_Color.getTheme(getApplicationContext()) == THEME_FALL) {
            setTheme(R.style.OverlayThemeFall);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.fall_brown));
            }
        } else if (Home_Color.getTheme(getApplicationContext()) == THEME_DARK) {
            setTheme(R.style.OverlayThemeDark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.dark_navy));
            }
        } else if (Home_Color.getTheme(getApplicationContext()) == THEME_ICE) {
            setTheme(R.style.OverlayThemeBlue);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.ice_dark_blue));
            }
        }
    }

}
