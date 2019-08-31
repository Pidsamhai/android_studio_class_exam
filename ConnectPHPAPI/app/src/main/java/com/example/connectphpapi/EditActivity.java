package com.example.connectphpapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    private EditText e_name, e_phone, e_faculty, e_major, e_username, e_password;
    private Button b_edit;
    private String i_name, i_phone, i_faculty, i_major, i_username, i_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        e_name = findViewById(R.id.e_name);
        e_phone = findViewById(R.id.e_phone);
        e_faculty = findViewById(R.id.e_faculty);
        e_major = findViewById(R.id.e_major);
        e_username = findViewById(R.id.e_username);
        e_password = findViewById(R.id.e_password);
        b_edit = findViewById(R.id.b_edit);
    }
}
