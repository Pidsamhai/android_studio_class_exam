package com.example.switchactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.MESSAGE1);
        TextView textView = findViewById(R.id.txt1);
        textView.setText(msg);
    }

    public void onClick(View view){
        EditText editText = findViewById(R.id.editText2);
        String msg = editText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(MainActivity.MESSAGE2, msg);
        setResult(RESULT_OK, intent);
        finish();
    }
}
