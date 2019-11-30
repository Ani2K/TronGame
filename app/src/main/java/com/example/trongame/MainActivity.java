package com.example.trongame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button rightTurnLeftPlayer = findViewById(R.id.rightTurnLeftPlayer);
        rightTurnLeftPlayer.setOnClickListener(v -> {
            System.out.println("Right Turn Left Player");
        });

        Button leftTurnLeftPlayer = findViewById(R.id.leftTurnLeftPlayer);
        leftTurnLeftPlayer.setOnClickListener(v -> {
            System.out.println("Left Turn Left Player");
        });

        Button rightTurnRightPlayer = findViewById(R.id.rightTurnRightPlayer);
        rightTurnRightPlayer.setOnClickListener(v -> {
            System.out.println("Right Turn Right Player");
        });

        Button leftTurnRightPlayer = findViewById(R.id.leftTurnRightPlayer);
        leftTurnRightPlayer.setOnClickListener(v -> {
            System.out.println("Left Turn Right Player");
        });
    }
}
