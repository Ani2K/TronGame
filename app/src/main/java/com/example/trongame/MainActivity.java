package com.example.trongame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button rightTurnLeft = findViewById(R.id.rightTurnLeft);
        rightTurnLeft.setOnClickListener(v -> {
            System.out.println("Right Turn Left");
        });

        Button leftTurnLeft = findViewById(R.id.leftTurnLeft);
        leftTurnLeft.setOnClickListener(v -> {
            System.out.println("Left Turn Left");
        });

        Button rightTurnRight = findViewById(R.id.rightTurnRight);
        rightTurnRight.setOnClickListener(v -> {
            System.out.println("Right Turn Right");
        });

        Button leftTurnRight = findViewById(R.id.leftTurnRight);
        leftTurnRight.setOnClickListener(v -> {
            System.out.println("Left Turn Right");
        });
    }
}
