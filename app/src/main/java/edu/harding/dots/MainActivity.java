package edu.harding.dots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private String bgColor = "#646464";
    private Boolean isColorBlind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load the preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("test", "background = " + prefs.getString("pref_background_color", "?"));
        bgColor = prefs.getString("pref_background_color", "?");
        isColorBlind = prefs.getBoolean("colorblind_mode", isColorBlind);
    }

    public void timedGameClick(View view) {
        String timedString = "Timed";
        // Launch GameActivity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("extraGameType", timedString);
        if (bgColor == "?") {bgColor = "#646464";}
        intent.putExtra("bgColor", bgColor);
        intent.putExtra("isColorBlind", isColorBlind);
        startActivity(intent);
    }

    public void movesGameClick(View view) {
        String movesString = "Moves";
        // Launch GameActivity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("extraGameType", movesString);
        intent.putExtra("bgColor", bgColor);
        startActivity(intent);
    }

    public void settingsClick(View view) {
        // Launch SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
