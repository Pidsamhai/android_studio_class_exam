package com.example.switchactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String MESSAGE1 = "Message1";
    public final static String MESSAGE2 = "Message2";
    public static final int CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        EditText editText = findViewById(R.id.edit_text);
        String msg = editText.getText().toString();
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(MESSAGE1,msg);
        //startActivity(intent);
        startActivityForResult(intent,CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE){
            if(resultCode == RESULT_OK){
                String msg = data.getStringExtra(MESSAGE2);
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            }
        }
    }

}
