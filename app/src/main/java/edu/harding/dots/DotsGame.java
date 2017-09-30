package edu.harding.dots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by fmccown on 9/5/2017.
 *
 * Some of the game logic was derived from https://github.com/SteinarArnason/Dots/
 */

public class DotsGame {

    public static int NUM_COLORS = 5;
    public static int NUM_CELLS = 6;
    public static int INIT_MOVES = 15;
    public static int INIT_TIME = 30;

    public enum AddDotStatus { Added, Rejected, Removed, CompleteCycle };

    public enum GameTypes { Timed, Moves };
    private GameTypes mGameType;

    private int mScore;
    public int mMoves;
    public int mTimer;

    private int mNumCells = NUM_CELLS;
    private Dot[][] mDots;

    private int mDotColors[];

    private ArrayList<Dot> mDotPath;

    public DotsGame(String gameType) {
        if ("Timed".equals(gameType))
        {
            mGameType = GameTypes.Timed;
            mTimer = INIT_TIME;
        }
        else if ("Moves".equals(gameType))
        {
            mGameType = GameTypes.Moves;
            mMoves = INIT_MOVES;
        }
        mScore = 0;

        mDotColors = new int[NUM_COLORS];
        for (int i = 0; i < NUM_COLORS; i++) {
            mDotColors[i] = i;
        }

        mDots = new Dot[mNumCells][mNumCells];

        for (int row = 0; row < mNumCells; row++) {
            for (int col = 0; col < mNumCells; col++) {
                mDots[row][col] = new Dot(row, col);
            }
        }

        mDotPath = new ArrayList();
    }

    public void newGame() {
        mScore = 0;

        for (int row = 0; row < mNumCells; row++) {
            for (int col = 0; col < mNumCells; col++) {
                mDots[row][col].changeColor();
            }
        }
    }

    public String getGameType() {
        if (mGameType == GameTypes.Moves)
        {
            String tempGameType = "Moves";
            return tempGameType;
        }
        if (mGameType == GameTypes.Timed)
        {
            String tempGameType = "Timed";
            return tempGameType;
        }
        String tempGameType = "None";
        return tempGameType;
    }

    public String getMoves(){
        String moveCount = ("" + mMoves);
        return moveCount;
    }

    public String getTime(){
        String timeCount = ("" + mTimer);
        return timeCount;
    }

    public int getScore() {
        return mScore;
    }

    public Dot getDot(int row, int col) {
        return mDots[row][col];
    }

    public ArrayList<Dot> getDotPath() {
        return mDotPath;
    }

    // Sort by rows
    private ArrayList<Dot> getSortedDotPath() {
        Collections.sort(mDotPath, new Comparator<Dot>() {
            public int compare(Dot dot1, Dot dot2) {
                return Integer.compare(dot1.row, dot2.row);
            }
        });

        return mDotPath;
    }

    public void clearDotPath() {
        for (Dot dot: mDotPath) {
            dot.selected = false;
        }
        mDotPath.clear();
    }

    public AddDotStatus addDotToPath(Dot dot) {
        if (mDotPath.size() == 0) {
            mDotPath.add(dot);
            dot.selected = true;
            return AddDotStatus.Added;
        }
        else {
            Dot lastDot = mDotPath.get(mDotPath.size() - 1);

            if (!mDotPath.contains(dot)) {
                // New dot encountered
                if (lastDot.color == dot.color && lastDot.isAdjacent(dot)) {
                    mDotPath.add(dot);
                    dot.selected = true;
                    return AddDotStatus.Added;
                }
            }
            else if (mDotPath.size() > 1) {
                // Backtracking or cycle
                Dot secondLast = mDotPath.get(mDotPath.size() - 2);

                // Backtracking
                if (secondLast.equals(dot)) {
                    Dot removedDot = mDotPath.remove(mDotPath.size() - 1);
                    removedDot.selected = false;
                    return AddDotStatus.Removed;
                }
                else if (!lastDot.equals(dot) && lastDot.isAdjacent(dot)) {
                    // Made cycle, so add all dots of same color to path
                    mDotPath.clear();
                    for (int row = 0; row < mNumCells; row++) {
                        for (int col = 0; col < mNumCells; col++) {
                            Dot currentDot = getDot(row, col);
                            if (currentDot.color == dot.color) {
                                dot.selected = true;
                                mDotPath.add(currentDot);
                            }
                        }
                    }

                    return AddDotStatus.CompleteCycle;
                }
            }
        }

        return AddDotStatus.Rejected;
    }

    public void finishMove() {
        if (mDotPath.size() > 1) {
            // Move all dots above each dot in the path down
            for (Dot dot : getSortedDotPath()) {
                dot.selected = false;
                // Put new dots in place
                for (int row = dot.row; row > 0; row--) {
                    Dot dotToMove = getDot(row, dot.col);
                    Dot dotAbove = getDot(row - 1, dot.col);
                    dotToMove.color = dotAbove.color;
                }
                Dot topDot = getDot(0, dot.col);
                topDot.changeColor();
            }

            mScore += mDotPath.size();
            if (mGameType == GameTypes.Moves)
            {
                mMoves--;
            }
            else if (mGameType == GameTypes.Timed)
            {
                mTimer--;
            }
        }
    }
}
