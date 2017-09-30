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

    final private int REQUEST_CODE_SETTINGS = 1;

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

    }

    public void timedGameClick(View view) {
        String timedString = "Timed";
        // Launch GameActivity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("extraGameType", timedString);
        startActivity(intent);
    }

    public void movesGameClick(View view) {
        String movesString = "Moves";
        // Launch GameActivity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("extraGameType", movesString);
        startActivity(intent);
    }

    public void settingsClick(View view) {
        // Launch SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTINGS) {
            String colorId = data.getStringExtra("newBackground");
            ContextCompat.getColor(this, Integer.parseInt(colorId));
            Log.d("test", colorId);
        }
    }
}
