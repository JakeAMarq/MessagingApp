package edu.uw.tcss450.team4projectclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Activity containing NavHostFragment for res/navigation/auth_graph
 */
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }
}
