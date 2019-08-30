package com.example.minitest10point;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void single_choice(View view){
        Intent intent = new Intent(this,SingleChoiceActivity.class);
        startActivity(intent);
    }
    public void custom_layout(View view){
        Intent intent = new Intent(this,CustomLayoutActivity.class);
        startActivity(intent);
    }
    public void spinner_string(View view){
        Intent intent = new Intent(this,SpinnerStringActivity.class);
        startActivity(intent);
    }
    public void call_activity(View view){
        Intent intent = new Intent(this,CallActivityActivity.class);
        startActivity(intent);
    }
}
