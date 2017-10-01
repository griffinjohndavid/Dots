package edu.harding.dots;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.media.SoundPool;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.parseColor;

public class GameActivity extends AppCompatActivity {

    private DotsGame mGame;

    private TextView[] mGameTextViews;
    private TextView mGameTimerValue;
    private TextView mGameTimer;
    private TextView mScoreValue;
    private Button mScoreButton;

    private CountDownTimer mCountDownTimer;


    private SensorManager sm;

    private float acelVal;
    private float acelLas;
    private float shake;

    private String mGameType;

    private String RED = "×";
    private String GREEN = "+";
    private String BLUE = "Δ";
    private String YELLOW = "–";
    private String PURPLE = "~";

    private Integer defaultTime = DotsGame.INIT_TIME;
    private Integer defaultMoves = DotsGame.INIT_MOVES;

    private Boolean mIsColorBlind = false;


    public SoundPool mSoundPool;
    private ArrayList<Integer> mSoundIds;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fate_in);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLas = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        mSoundPool = createSoundPool();
        mSoundIds = new ArrayList<>();
        mSoundIds.add(mSoundPool.load(this, R.raw.background, 1));
        mSoundIds.add(mSoundPool.load(this, R.raw.finish, 1));
        mSoundIds.add(mSoundPool.load(this, R.raw.deselect, 1));
        
        this.findViewById(android.R.id.content).setBackgroundColor(parseColor(getIntent().getStringExtra("bgColor")));

        mIsColorBlind = getIntent().getBooleanExtra("isColorBlind", false);

        mGameTimerValue = (TextView) findViewById(R.id.timeValue);
        mGameTimer = (TextView) findViewById(R.id.time);
        mScoreValue = (TextView) findViewById(R.id.scoreValue);
        mScoreButton = (Button) findViewById(R.id.shareHiscore);

        mGameTextViews = new TextView[(DotsGame.NUM_CELLS * DotsGame.NUM_CELLS)];
            
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gameBoard);
        gridLayout.startAnimation(animation1);
        
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            mGameTextViews[i] = (TextView) gridLayout.getChildAt(i);
        }
        gridLayout.setOnTouchListener(GridTouchListener);

        // Is activity being re-created?
        if (savedInstanceState == null) {
            if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
            {
                gameModeTimed(defaultTime);
            }
            if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
            {
                gameModeMoves(defaultMoves);
            }
            mGame = new DotsGame(mGameType);
            mScoreValue.setText(mGame.getScore());
            drawBoard();
        }
        else {

            mGame = new DotsGame(mGameType);
            // Restore game state
            if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
            {
                gameModeTimed(savedInstanceState.getInt("timeLeft"));
                mGame.mTimer = savedInstanceState.getInt("timeLeft");
            }
            if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
            {
                gameModeMoves(savedInstanceState.getInt("movesLeft"));
                mGame.mMoves = savedInstanceState.getInt("movesLeft");
            }
            mGame.setScore(savedInstanceState.getString("scoreAmount"));
            mScoreValue.setText(mGame.getScore());
            ArrayList<Integer> boardState = savedInstanceState.getIntegerArrayList("boardState");
            mGame.restoreState(boardState);
            if (mGame.isGameOver()) {mScoreButton.setVisibility(View.VISIBLE);}
            drawBoard();
        }

    }

    public void gameModeMoves(Integer movesLeft)
    {
        mGameType = "Moves";
        String movesLeftValue = (movesLeft + "");
        mGameTimerValue.setText(movesLeftValue);
        mGameTimer.setText(R.string.movesText);
    }

    public void gameModeTimed(Integer timeLength)
    {
        mGameType = "Timed";
        String timeValueToSet = (defaultTime + "");
        mGameTimerValue.setText(timeValueToSet);
        countdownTimer(timeLength * 1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the board's state
        ArrayList<Integer> boardState = mGame.getBoardState();
        outState.putIntegerArrayList("boardState", boardState);
        outState.putInt("timeLeft", mGame.mTimer);
        outState.putInt("movesLeft", mGame.mMoves);
        outState.putString("scoreAmount", mGame.getScore());
    }
    
        protected SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createNewSoundPool();
        } else {
            return createOldSoundPool();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected SoundPool createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected SoundPool createOldSoundPool(){
        return new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }



    private void countdownTimer(Integer timeLength) {
        // code from https://developer.android.com/reference/android/os/CountDownTimer.html
        mCountDownTimer = new CountDownTimer(timeLength, 1000) {

            public void onTick(long millisUntilFinished) {
                String timeValueToSet = ("" + millisUntilFinished / 1000);
                mGameTimerValue.setText(timeValueToSet);
                mGame.timerTick();
            }

            public void onFinish() {
                String timeValueToSet = ("" + 0);
                mGameTimerValue.setText(timeValueToSet);
                mGame.gameOver();
                if (mGame.isGameOver()) {mScoreButton.setVisibility(View.VISIBLE);
                    mSoundPool.play(mSoundIds.get(1), 1, 1, 1, 0, 1);
                }
            }
        }.start();
    }

    public void newGameClick(View view) {
        mGame = new DotsGame(mGameType);
        mCountDownTimer.cancel();
        if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
        {
            gameModeTimed(defaultTime);
        }
        if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
        {
            gameModeMoves(defaultMoves);
        }
        mScoreValue.setText(mGame.getScore());
        drawBoard();
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fate_in);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gameBoard);

        gridLayout.startAnimation(animation1);
        drawBoard();
        mScoreButton.setVisibility(View.INVISIBLE);
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
                        if (mIsColorBlind) {mGameTextViews[i].setText(RED);}
                        if (mGame.getDot(row, col).selected) {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_red_selected));
                        }
                        else {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_red));
                        }
                    }
                    else if (mGame.getDot(row, col).color == 1)
                    {
                        if (mIsColorBlind) {mGameTextViews[i].setText(GREEN);}
                        if (mGame.getDot(row, col).selected) {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_green_selected));
                        }
                        else {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_green));
                        }
                    }
                    else if (mGame.getDot(row, col).color == 2)
                    {
                        if (mIsColorBlind) {mGameTextViews[i].setText(BLUE);}
                        if (mGame.getDot(row, col).selected) {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_blue_selected));
                        }
                        else {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_blue));
                        }
                    }
                    else if (mGame.getDot(row, col).color == 3)
                    {
                        if (mIsColorBlind) {mGameTextViews[i].setText(YELLOW);}
                        if (mGame.getDot(row, col).selected) {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_yellow_selected));
                        }
                        else {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_yellow));
                        }
                    }
                    else if (mGame.getDot(row, col).color == 4)
                    {
                        if (mIsColorBlind) {mGameTextViews[i].setText(PURPLE);}
                        if (mGame.getDot(row, col).selected) {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_purple_selected));
                        }
                        else {
                            mGameTextViews[i].setBackground(ContextCompat.getDrawable(this, R.drawable.dot_purple));
                        }
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
            if ((col < DotsGame.NUM_CELLS && col > -1) && (row < DotsGame.NUM_CELLS && row > -1))
            {
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
                    mGame.checkGameOver();
                    mScoreValue.setText(mGame.getScore());
                    if (mGame.getGameType().equals("Moves"))
                    {
                        mGameTimerValue.setText(mGame.getMoves());
                    }
                    mGame.clearDotPath();
                    mSoundPool.play(mSoundIds.get(2), 1, 1, 1, 0, 1);
                    if (mGame.isGameOver()) {
                        mScoreButton.setVisibility(View.VISIBLE);
                        mSoundPool.play(mSoundIds.get(1), 1, 1, 1, 0, 1);

                    }

                    drawBoard();
                    return true;
                }
            }

            return false;
        }
    };

    public void shareScore(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);

            // Supply extra that is plain text
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Dots Highscore");
        intent.putExtra(Intent.EXTRA_TEXT, "My highscore in Dots was " + mScoreValue.getText());

        // If at least one app can handle intent, allow user to choose
        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent chooser = intent.createChooser(intent, "Share Highscore");
            startActivity(chooser);
        }
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLas = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x +  y*y + z*z));
            float delta = acelVal - acelLas;
            shake = shake * 0.9f + delta;

            if (shake > 6){
                mGame = new DotsGame(mGameType);
                mCountDownTimer.cancel();
                if ("Timed".equals(getIntent().getStringExtra("extraGameType")))
                {
                    gameModeTimed(defaultTime);
                }
                if ("Moves".equals(getIntent().getStringExtra("extraGameType")))
                {
                    gameModeMoves(defaultMoves);
                }
                mScoreValue.setText(mGame.getScore());
                drawBoard();
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fate_in);

                GridLayout gridLayout = (GridLayout) findViewById(R.id.gameBoard);

                gridLayout.startAnimation(animation1);
                drawBoard();
                mScoreButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}
