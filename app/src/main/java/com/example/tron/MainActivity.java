package com.example.tron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        Intent intent = new Intent(this, NewGameActivity.class);
        Spinner settings = findViewById(R.id.settings);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grid_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settings.setAdapter(adapter);
        intent.putExtra("rows", 70);
        intent.putExtra("columns", 90);
        settings.setSelection(1);

        settings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                if (position == 0) {
                    intent.putExtra("rows", 30);
                    intent.putExtra("columns", 50);
                } else if (position == 1) {
                    intent.putExtra("rows", 50);
                    intent.putExtra("columns", 70);
                } else if (position == 2) {
                    intent.putExtra("rows", 70);
                    intent.putExtra("columns", 80);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        Button startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> startActivity(intent));
    }
}
