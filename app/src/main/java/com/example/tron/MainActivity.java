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
import java.util.List;

//https://stackoverflow.com/questions/35692984/programmatically-adding-textview-to-grid-layout-alignment-not-proper/53948318#53948318
//https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        setUpNewGame();
        startGameplay();
    }

    private void startGameplay() {
        //calls gameUpdate every delayMilliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                gameUpdate();
                handler.postDelayed(this, delayMilliseconds);
            }
        }, delayMilliseconds);
    }

    private void endGameplay() {
        //handler.removeCallbacks(handler);
    }

    private void setUpNewGame() {
        //clear previous paths
        player1Path.clear();
        player2Path.clear();

        //adds initial position to paths of each player
        player1Path.add(player1StartingCoordinate);
        player2Path.add(player2StartingCoordinate);
        //updates graphics of trail
        updatePlayerTrailsGraphics();
    }

    private Button getButton(int row, int column) {
        return buttonArena[row][column];
    }

    private void updateColor(int row, int column, int color) {
        getButton(row, column).setBackgroundColor(color);
    }

    private void gameUpdate() {
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
        player1Path.add(player1NextLocation);

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
        player2Path.add(player2NextLocation);

        //update previous direction
        player1PreviousDirection = player1CurrentDirection;
        player2PreviousDirection = player2CurrentDirection;

        //update graphics of player trails
        updatePlayerTrailsGraphics();

        //check for end game (collisions with either trail, collisions with arena wall

        //if game ends - update buttons to show winner, update buttons to allow game to restart
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

    //need method to update direction based on button press
    private void player1LeftButtonPressed() {
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
    private void player1RightButtonPressed() {
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
    private void player2LeftButtonPressed() {
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
    private void player2RightButtonPressed() {
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
