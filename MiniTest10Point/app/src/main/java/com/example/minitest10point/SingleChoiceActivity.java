package com.example.minitest10point;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SingleChoiceActivity extends AppCompatActivity {

    Button b_dialog,b_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_choice);
        b_dialog = findViewById(R.id.b_dialog);
        b_back = findViewById(R.id.b_back);

        final String[] items = {"2554","2555","2556","2557"};

        b_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SingleChoiceActivity.this)
                        .setTitle("Dialog Single Choice")
                        .setSingleChoiceItems(items,-1,null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ListView listView = ((AlertDialog) dialogInterface).getListView();
                                int p = listView.getCheckedItemPosition();
                                if(p>=0) Toast.makeText(SingleChoiceActivity.this,items[p],Toast.LENGTH_LONG).show();
                            }
                        }).show();
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
