package edu.harding.dots;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void highscoresClick(View view) {

    }

    public void settingsClick(View view) {
        // Launch SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }
}
