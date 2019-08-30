package com.example.minitest10point;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SpinnerStringActivity extends AppCompatActivity {

    Spinner spinner1;
    Button b_spinner,b_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_string);
        spinner1 = findViewById(R.id.spinner1);
        b_spinner = findViewById(R.id.b_spinner);
        b_back = findViewById(R.id.b_back);

        b_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpinnerStringActivity.this,"Your Selected "+spinner1.getSelectedItem(),Toast.LENGTH_LONG).show();
            }
        });

        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
