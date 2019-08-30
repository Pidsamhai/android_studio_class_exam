package com.example.minitest10point;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CustomLayoutActivity extends AppCompatActivity {
    Button b_dialog,b_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_layout);
        b_dialog = findViewById(R.id.b_dialog);
        b_back = findViewById(R.id.b_back);

        b_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                final AlertDialog builder = new AlertDialog.Builder(CustomLayoutActivity.this)
                        .setTitle("Custom Layout")
                        .setView(inflater.inflate(R.layout.custom_layout,null))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                builder.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = builder.getButton(builder.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText editText = builder.findViewById(R.id.username);
                                String str = editText.getText().toString();
                                Toast.makeText(CustomLayoutActivity.this,str,Toast.LENGTH_LONG).show();
                                builder.dismiss();
                            }
                        });
                    }
                });
                builder.show();
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
