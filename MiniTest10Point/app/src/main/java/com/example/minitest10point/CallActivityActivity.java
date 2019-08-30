package com.example.minitest10point;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CallActivityActivity extends AppCompatActivity {
    Button b_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_activity);
        b_back = findViewById(R.id.b_back);
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void firstActivity(View view){
        Intent intent = new Intent(this,FirstActivity.class);
        startActivity(intent);
    }
    public void secondActivity(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }
}
