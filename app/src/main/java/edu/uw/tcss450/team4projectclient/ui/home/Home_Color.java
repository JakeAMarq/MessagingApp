package edu.uw.tcss450.team4projectclient.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.uw.tcss450.team4projectclient.MainActivity;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.ActivityHomeColorBinding;
import edu.uw.tcss450.team4projectclient.databinding.ActivityMainBinding;
import me.pushy.sdk.lib.jackson.databind.ser.Serializers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Activity;
import android.view.ViewGroup;

public class Home_Color extends BaseActivity implements View.OnClickListener {
    private ActivityHomeColorBinding binding;
    public static int currentTheme = R.style.OverlayThemeBanana;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_color);
        binding = ActivityHomeColorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addListeners();

    }

    private void addListeners() {
        binding.buttonBanana.setOnClickListener(this);
        binding.buttonIce.setOnClickListener(this);
        binding.buttonFall.setOnClickListener(this);
        binding.buttonDark.setOnClickListener(this);
        binding.buttonVibrant.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.buttonBanana) {
            setTheme(getApplicationContext(), 1);
            recreateActivity();
            currentTheme = R.style.OverlayThemeBanana;
        } else if (v == binding.buttonIce) {
            setTheme(getApplicationContext(), 5);
            recreateActivity();
            currentTheme = R.style.OverlayThemeBlue;
        } else if (v == binding.buttonFall) {
            setTheme(getApplicationContext(), 3);
            recreateActivity();
            currentTheme = R.style.OverlayThemeFall;
        } else if (v == binding.buttonDark) {
            setTheme(getApplicationContext(), 4);
            recreateActivity();
            currentTheme = R.style.OverlayThemeDark;
        } else if (v == binding.buttonVibrant) {
            setTheme(getApplicationContext(), 2);
            recreateActivity();
            currentTheme = R.style.OverlayThemeVibrant;
        }
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply();

    }
    public static int getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.prefs_theme_key), -1);
    }

}
