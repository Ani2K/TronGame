package com.example.tron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//https://stackoverflow.com/questions/35692984/programmatically-adding-textview-to-grid-layout-alignment-not-proper/53948318#53948318
//https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
//https://stackoverflow.com/questions/4849051/using-contains-on-an-arraylist-with-integer-arrays

//need to implement: start new game
//check collisions/endstate/end game
//(re)start new game

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; //tag for logcat

    private int delayMilliseconds = 100; //milliseconds between each game update

    private int buttonSize = 10; //size of button in pixels (i think)
    private int ROWS = 70; //rows of arena
    private int COLUMNS = 90; //columns of arena
    private Button[][] buttonArena = new Button[ROWS][COLUMNS];

    private int gridPadding = 2; //spacing between each button
    private int gridColor = Color.LTGRAY; //base color of arena

    private int player1Color = Color.BLUE; //color of player 1 trail
    private int player2Color = Color.RED; //color of player 2 trail

    //starting coordinate: int[] of size 2, first value is row, second value is column
    private int[] player1StartingCoordinate = new int[]{10, 10};
    private int[] player2StartingCoordinate = new int[]{ROWS - 1 - 10, COLUMNS - 1 - 10};

    //the actual values are arbitrary, they just serve to identify the directions
    private int NORTH = 0;
    private int EAST = 1;
    private int SOUTH = 2;
    private int WEST = 3;

    private int GAME_END_TIE = 0;
    private int GAME_END_PLAYER_1_WINS = 1;
    private int GAME_END_PLAYER_2_WINS = 2;

    //initial direction of each player
    private int player1StartingDirection = EAST;
    private int player2StartingDirection = WEST;

    //stores previous direction of each player to make it easier to determine current direction
    private int player1PreviousDirection = player1StartingDirection;
    private int player2PreviousDirection = player2StartingDirection;

    //current direction of each player
    private int player1CurrentDirection = player1StartingDirection;
    private int player2CurrentDirection = player2StartingDirection;

    //stores every coordinate each player has visited using int[] of size 2 (first value is row, second value is column)
    private List<int[]> player1Path = new ArrayList<>();
    private List<int[]> player2Path = new ArrayList<>();

    //stores whether game is running
    private boolean isGameRunning = false;

    final Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runnable = new Runnable(){
            public void run(){
                gameUpdate();
                handler.postDelayed(this, delayMilliseconds);
            }
        };

        androidx.gridlayout.widget.GridLayout arena = findViewById(R.id.arenaGridLayout);
        //arena.setAlignmentMode(GridLayout.ALIGN_BOUNDS);\

        arena.setColumnCount(COLUMNS);
        arena.setRowCount(ROWS);

        for (int currentRow = 0; currentRow < ROWS; currentRow++) { //sets up initial arena
            for (int currentColumn = 0; currentColumn < COLUMNS; currentColumn++) {
                buttonArena[currentRow][currentColumn] = new Button(this);
                //button.setText(currentRow + "|" + currentColumn);
                updateColor(currentRow, currentColumn, gridColor);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                //Log.d(TAG, currentRow + "|" + currentColumn + "test0 width " + params.width + " | height " + params.height);

                params.height = buttonSize;
                params.width = buttonSize;

                //Log.d(TAG, currentRow + "|" + currentColumn + "test1 width " + params.width + " | height " + params.height);

                params.bottomMargin = gridPadding;
                params.topMargin = gridPadding;
                params.leftMargin = gridPadding;
                params.rightMargin = gridPadding;

                //Log.d(TAG, currentRow + "|" + currentColumn + "test2 width " + params.width + " | height " + params.height);

                //params.rowSpec = GridLayout.spec(currentRow, 1, 1);
                //params.columnSpec = GridLayout.spec(currentColumn, 1, 1);

                //Log.d(TAG, currentRow + "|" + currentColumn + "test3 width " + params.width + " | height " + params.height);

                //params.width = Math.min(params.width, params.height);
                //params.height = Math.min(params.width, params.height);

                arena.addView(buttonArena[currentRow][currentColumn], params);
            }
        }

        //makes buttons functional
        findViewById(R.id.player1LeftButton).setOnClickListener(unused -> player1LeftButtonPressed());
        findViewById(R.id.player1RightButton).setOnClickListener(unused -> player1RightButtonPressed());
        findViewById(R.id.player2LeftButton).setOnClickListener(unused -> player2LeftButtonPressed());
        findViewById(R.id.player2RightButton).setOnClickListener(unused -> player2RightButtonPressed());

        ((Button) findViewById(R.id.player1LeftButton)).setText("Left\n(Press\nto\nstart)");
        ((Button) findViewById(R.id.player1RightButton)).setText("Right\n(Press\nto\nstart)");
        ((Button) findViewById(R.id.player2LeftButton)).setText("Left\n(Press\nto\nstart)");
        ((Button) findViewById(R.id.player2RightButton)).setText("Right\n(Press\nto\nstart)");

        findViewById(R.id.player1LeftButton).setBackgroundColor(player1Color);
        findViewById(R.id.player1RightButton).setBackgroundColor(player1Color);
        findViewById(R.id.player2LeftButton).setBackgroundColor(player2Color);
        findViewById(R.id.player2RightButton).setBackgroundColor(player2Color);

        setUpNewGame();

    }

    private void startGameplay() {
        isGameRunning = true;

        //calls gameUpdate every delayMilliseconds
        handler.postDelayed(runnable, delayMilliseconds);
    }

    private void endGameplay() {
        isGameRunning = false;
        handler.removeCallbacks(runnable);
    }

    private void setUpNewGame() {
        //clear previous paths
        player1Path.clear();
        player2Path.clear();
        resetArenaGraphics();

        //reset directions
        player1PreviousDirection = player1StartingDirection;
        player1CurrentDirection = player1StartingDirection;
        player2PreviousDirection = player2StartingDirection;
        player2CurrentDirection = player2StartingDirection;

        //adds initial position to paths of each player
        player1Path.add(player1StartingCoordinate);
        player2Path.add(player2StartingCoordinate);
        //updates graphics of trail
        updatePlayerTrailsGraphics();


    }

    //0 for tie, 1 for player 1, 2 for player 2
    private void setUpEndGame(int winner) {
        //stuff like updating text
        if (winner == GAME_END_TIE) {
            ((Button) findViewById(R.id.player1LeftButton)).setText("Tie\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player1RightButton)).setText("Tie\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2LeftButton)).setText("Tie\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2RightButton)).setText("Tie\n(Press\nto\nrestart)");
        } else if (winner == GAME_END_PLAYER_1_WINS) {
            ((Button) findViewById(R.id.player1LeftButton)).setText("Win\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player1RightButton)).setText("Win\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2LeftButton)).setText("Lose\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2RightButton)).setText("Lose\n(Press\nto\nrestart)");
        } else if (winner == GAME_END_PLAYER_2_WINS) {
            ((Button) findViewById(R.id.player1LeftButton)).setText("Lose\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player1RightButton)).setText("Lose\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2LeftButton)).setText("Win\n(Press\nto\nrestart)");
            ((Button) findViewById(R.id.player2RightButton)).setText("Win\n(Press\nto\nrestart)");
        } else {
            Log.d(TAG, "Winner error");
        }

        endGameplay();
    }

    private Button getButton(int row, int column) {
        return buttonArena[row][column];
    }

    private void updateColor(int row, int column, int color) {
        getButton(row, column).setBackgroundColor(color);
    }

    private void gameUpdate() {
        //update button text
        ((Button) findViewById(R.id.player1LeftButton)).setText("Left");
        ((Button) findViewById(R.id.player1RightButton)).setText("Right");
        ((Button) findViewById(R.id.player2LeftButton)).setText("Left");
        ((Button) findViewById(R.id.player2RightButton)).setText("Right");

        //move trail one block
        int[] player1CurrentLocation = player1Path.get(player1Path.size() - 1);
        int[] player1NextLocation = new int[2];
        if (player1CurrentDirection == NORTH) {
            player1NextLocation = new int[]{player1CurrentLocation[0] - 1, player1CurrentLocation[1] + 0};
        } else if (player1CurrentDirection == EAST) {
            player1NextLocation = new int[]{player1CurrentLocation[0] + 0, player1CurrentLocation[1] + 1};
        } else if (player1CurrentDirection == SOUTH) {
            player1NextLocation = new int[]{player1CurrentLocation[0] + 1, player1CurrentLocation[1] + 0};
        } else if (player1CurrentDirection == WEST) {
            player1NextLocation = new int[]{player1CurrentLocation[0] + 0, player1CurrentLocation[1] - 1};
        } else {
            Log.d(TAG, "Direction error");
        }


        int[] player2CurrentLocation = player2Path.get(player2Path.size() - 1);
        int[] player2NextLocation = new int[2];
        if (player2CurrentDirection == NORTH) {
            player2NextLocation = new int[]{player2CurrentLocation[0] - 1, player2CurrentLocation[1] + 0};
        } else if (player2CurrentDirection == EAST) {
            player2NextLocation = new int[]{player2CurrentLocation[0] + 0, player2CurrentLocation[1] + 1};
        } else if (player2CurrentDirection == SOUTH) {
            player2NextLocation = new int[]{player2CurrentLocation[0] + 1, player2CurrentLocation[1] + 0};
        } else if (player2CurrentDirection == WEST) {
            player2NextLocation = new int[]{player2CurrentLocation[0] + 0, player2CurrentLocation[1] - 1};
        } else {
            Log.d(TAG, "Direction error");
        }


        //update previous direction
        player1PreviousDirection = player1CurrentDirection;
        player2PreviousDirection = player2CurrentDirection;

        //check for end game (collisions with either trail, collisions with arena wall
        //if game ends - update buttons to show winner, update buttons to allow game to restart
        boolean player1Alive = true;
        if (player1NextLocation[0] < 0 || player1NextLocation[0] >= ROWS || player1NextLocation[1] < 0 || player1NextLocation[1] >= COLUMNS
                || player1PathContains(player1NextLocation) || player2PathContains(player1NextLocation)) {
            player1Alive = false;
        }
        boolean player2Alive = true;
        if (player2NextLocation[0] < 0 || player2NextLocation[0] >= ROWS || player2NextLocation[1] < 0 || player2NextLocation[1] >= COLUMNS
                || player1PathContains(player2NextLocation) || player2PathContains(player2NextLocation)) {
            player2Alive = false;
        }
        if (!player1Alive && !player2Alive) {
            setUpEndGame(GAME_END_TIE);
        } else if (!player1Alive) {
            setUpEndGame(GAME_END_PLAYER_2_WINS);
        } else if (!player2Alive) {
            setUpEndGame(GAME_END_PLAYER_1_WINS);
        }


        //update graphics of player trails
        if (isGameRunning) {
            player1Path.add(player1NextLocation);
            player2Path.add(player2NextLocation);
            updatePlayerTrailsGraphics();
        }
    }
    private boolean player1PathContains(int[] input) {
        for(final int[] item : player1Path){
            if(Arrays.equals(item, input)){
                return true;
            }
        }
        return false;
    }
    private boolean player2PathContains(int[] input) {
        for(final int[] item : player2Path){
            if(Arrays.equals(item, input)){
                return true;
            }
        }
        return false;
    }
    private void updatePlayerTrailsGraphics() {
        for (int currentPlayer1CoordinateIndex = 0; currentPlayer1CoordinateIndex < player1Path.size(); currentPlayer1CoordinateIndex++) {
            int[] currentCoordinate = player1Path.get(currentPlayer1CoordinateIndex);
            updateColor(currentCoordinate[0], currentCoordinate[1], player1Color);
        }
        for (int currentPlayer2CoordinateIndex = 0; currentPlayer2CoordinateIndex < player2Path.size(); currentPlayer2CoordinateIndex++) {
            int[] currentCoordinate = player2Path.get(currentPlayer2CoordinateIndex);
            updateColor(currentCoordinate[0], currentCoordinate[1], player2Color);
        }
    }
    private void resetArenaGraphics() {
        for (int currentRow = 0; currentRow < ROWS; currentRow++) { //sets up initial arena
            for (int currentColumn = 0; currentColumn < COLUMNS; currentColumn++) {
                updateColor(currentRow, currentColumn, gridColor);
            }
        }
    }

    //need method to update direction based on button press
    private void player1LeftButtonPressed() {
        if (!isGameRunning) {
            setUpNewGame();
            startGameplay();
        } else {
            if (player1PreviousDirection == NORTH) {
                player1CurrentDirection = WEST;
            } else if (player1PreviousDirection == EAST) {
                player1CurrentDirection = NORTH;
            } else if (player1PreviousDirection == SOUTH) {
                player1CurrentDirection = EAST;
            } else if (player1PreviousDirection == WEST) {
                player1CurrentDirection = SOUTH;
            } else {
                Log.d(TAG, "Direction error");
            }
        }
    }
    private void player1RightButtonPressed() {
        if (!isGameRunning) {
            setUpNewGame();
            startGameplay();
        } else {
            if (player1PreviousDirection == NORTH) {
                player1CurrentDirection = EAST;
            } else if (player1PreviousDirection == EAST) {
                player1CurrentDirection = SOUTH;
            } else if (player1PreviousDirection == SOUTH) {
                player1CurrentDirection = WEST;
            } else if (player1PreviousDirection == WEST) {
                player1CurrentDirection = NORTH;
            } else {
                Log.d(TAG, "Direction error");
            }
        }
    }
    private void player2LeftButtonPressed() {
        if (!isGameRunning) {
            setUpNewGame();
            startGameplay();
        } else {
            if (player2PreviousDirection == NORTH) {
                player2CurrentDirection = WEST;
            } else if (player2PreviousDirection == EAST) {
                player2CurrentDirection = NORTH;
            } else if (player2PreviousDirection == SOUTH) {
                player2CurrentDirection = EAST;
            } else if (player2PreviousDirection == WEST) {
                player2CurrentDirection = SOUTH;
            } else {
                Log.d(TAG, "Direction error");
            }
        }
    }
    private void player2RightButtonPressed() {
        if (!isGameRunning) {
            setUpNewGame();
            startGameplay();
        } else {
            if (player2PreviousDirection == NORTH) {
                player2CurrentDirection = EAST;
            } else if (player2PreviousDirection == EAST) {
                player2CurrentDirection = SOUTH;
            } else if (player2PreviousDirection == SOUTH) {
                player2CurrentDirection = WEST;
            } else if (player2PreviousDirection == WEST) {
                player2CurrentDirection = NORTH;
            } else {
                Log.d(TAG, "Direction error");
            }
        }
    }
}
