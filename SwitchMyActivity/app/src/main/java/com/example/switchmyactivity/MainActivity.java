package com.example.switchmyactivity;

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

    public void secondActivity(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }
    public void thirdActivity(View view){
        Intent intent = new Intent(this,ThirdActivity.class);
        startActivity(intent);
    }
    public void fourthActivity(View view){
        Intent intent = new Intent(this, FourthActivity.class);
        startActivity(intent);
    }
    public void fifthActivity(View view){
        Intent intent = new Intent(this,FifthActivity.class);
        startActivity(intent);
    }
}
