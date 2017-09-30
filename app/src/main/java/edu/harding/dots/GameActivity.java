package edu.harding.dots;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import static android.graphics.Color.parseColor;

public class GameActivity extends AppCompatActivity {

    private DotsGame mGame;

    private TextView[] mGameTextViews;
    private TextView mGameTimerValue;
    private TextView mGameTimer;
    private TextView mScoreValue;

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

        this.findViewById(android.R.id.content).setBackgroundColor(parseColor(getIntent().getStringExtra("bgColor")));

        mGameTimerValue = (TextView) findViewById(R.id.timeValue);
        mGameTimer = (TextView) findViewById(R.id.time);
        mScoreValue = (TextView) findViewById(R.id.scoreValue);

        mGameTextViews = new TextView[(DotsGame.NUM_CELLS * DotsGame.NUM_CELLS)];

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gameBoard);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            mGameTextViews[i] = (TextView) gridLayout.getChildAt(i);
        }
        gridLayout.setOnTouchListener(GridTouchListener);
        if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
        {
            mGameType = "Timed";
            mGameTimerValue.setText(DotsGame.INIT_TIME + "");
            countdownTimer();
        }
        else if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
        {
            mGameType = "Moves";
            mGameTimerValue.setText(DotsGame.INIT_MOVES + "");
            mGameTimer.setText(R.string.movesText);
        }

        mGame = new DotsGame(mGameType);
        mScoreValue.setText(mGame.getScore());
        drawBoard();
    }

    private void countdownTimer() {
        // code from https://developer.android.com/reference/android/os/CountDownTimer.html
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mGameTimerValue.setText("" + millisUntilFinished / 1000);
                mGame.timerTick();
            }

            public void onFinish() {
                mGameTimerValue.setText("" + 0);
                mGame.gameOver();
            }
        }.start();
    }

    public void newGameClick(View view) {
        // May need more code?
        mGame = new DotsGame(mGameType);
        mScoreValue.setText(mGame.getScore());
        if (mGame.getGameType().equals("Moves"))
        {
            mGameTimerValue.setText(mGame.getMoves());
        }
        else if (mGame.getGameType().equals("Timed"))
        {
            mGameTimerValue.setText(mGame.getTime());
            countdownTimer();
        }
        drawBoard();
    }

    public void menuClick(View view) {
        super.onBackPressed();
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
                        mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_red));
                    }
                    else if (mGame.getDot(row, col).color == 1)
                    {
                        mGameTextViews[i].setText(GREEN);
                        mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_green));
                    }
                    else if (mGame.getDot(row, col).color == 2)
                    {
                        mGameTextViews[i].setText(BLUE);
                        mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_blue));
                    }
                    else if (mGame.getDot(row, col).color == 3)
                    {
                        mGameTextViews[i].setText(YELLOW);
                        mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_yellow));
                    }
                    else if (mGame.getDot(row, col).color == 4)
                    {
                        mGameTextViews[i].setText(PURPLE);
                        mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_purple));
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
                mScoreValue.setText(mGame.getScore());
                if (mGame.getGameType().equals("Moves"))
                {
                    mGameTimerValue.setText(mGame.getMoves());
                }
                mGame.clearDotPath();
                drawBoard();
                return true;
            }

            return false;
        }
    };
}
