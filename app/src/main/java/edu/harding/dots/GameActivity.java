package edu.harding.dots;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private DotsGame mGame;

    private TextView[] mGameTextViews;

    private String mGameType;

    private char RED = '×';
    private char GREEN = '+';
    private char BLUE = 'Δ';
    private char YELLOW = '–';
    private char PURPLE = '~';


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameTextViews = new TextView[(DotsGame.NUM_CELLS * DotsGame.NUM_CELLS)];

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gameBoard);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            mGameTextViews[i] = (TextView) gridLayout.getChildAt(i);
        }
        gridLayout.setOnTouchListener(GridTouchListener);
        if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
        {
            mGameType = "Timed";
        }
        else if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
        {
            mGameType = "Moves";
        }
        mGame = new DotsGame(mGameType);
    }

    public void newGameClick(View view) {
        // May need more code?
        mGame = new DotsGame(mGameType);
    }

    public void menuClick(View view) {

    }

    private View.OnTouchListener GridTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            // Figure out which textView was touched
            int cellWidth = view.getWidth() / 6 ;
            int cellHeight = view.getHeight() / 6;
            int x = (int) event.getX();
            int y = (int) event.getY();
            int col = x / cellWidth;
            int row = y / cellHeight;
            int index = row * 6 + col;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // add code for updating view
                mGame.addDotToPath(mGame.getDot(row, col));
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // add code for updating view
                mGame.addDotToPath(mGame.getDot(row, col));
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                // add code for updating view
                mGame.finishMove();
                mGame.clearDotPath();
                return true;
            }

            return false;
        }
    };
}
