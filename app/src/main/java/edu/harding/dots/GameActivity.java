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

    private String RED = "×";
    private String GREEN = "+";
    private String BLUE = "Δ";
    private String YELLOW = "–";
    private String PURPLE = "~";


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
        drawBoard();
    }

    public void newGameClick(View view) {
        // May need more code?
        mGame = new DotsGame(mGameType);
        drawBoard();
    }

    public void menuClick(View view) {

    }

    public void drawBoard() {
        int i = 0;
        for (int row = 0; row < DotsGame.NUM_CELLS; row++) {
            for (int col = 0; col < DotsGame.NUM_CELLS; col++) {
                if (i < mGameTextViews.length)
                {
                    if (mGame.getDot(row, col).color == 0)
                    {
                        mGameTextViews[i].setText(RED);
                    }
                    else if (mGame.getDot(row, col).color == 1)
                    {
                        mGameTextViews[i].setText(GREEN);
                    }
                    else if (mGame.getDot(row, col).color == 2)
                    {
                        mGameTextViews[i].setText(BLUE);
                    }
                    else if (mGame.getDot(row, col).color == 3)
                    {
                        mGameTextViews[i].setText(YELLOW);
                    }
                    else if (mGame.getDot(row, col).color == 4)
                    {
                        mGameTextViews[i].setText(PURPLE);
                    }
                    i++;
                }
            }
        }

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
                drawBoard();
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // add code for updating view
                mGame.addDotToPath(mGame.getDot(row, col));
                drawBoard();
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                // add code for updating view
                mGame.addDotToPath(mGame.getDot(row, col));
                mGame.finishMove();
                mGame.clearDotPath();
                drawBoard();
                return true;
            }

            return false;
        }
    };
}
