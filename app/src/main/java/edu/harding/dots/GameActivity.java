package edu.harding.dots;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity {

    private char RED = '×';
    private char GREEN = '+';
    private char BLUE = 'Δ';
    private char YELLOW = '–';
    private char PURPLE = '~';


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    public void newGameClick(View view) {

    }

    public void menuClick(View view) {

    }
}
