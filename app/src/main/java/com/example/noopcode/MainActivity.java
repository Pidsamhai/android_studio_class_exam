package com.example.noopcode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        Button btn5 = (Button) findViewById(R.id.btn5);
        Button btn6 = (Button) findViewById(R.id.btn6);
        Button btn7 = (Button) findViewById(R.id.btn7);




        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(" This is dialog 1");
                builder.setMessage("Are you OK?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"OK",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"NO",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });



        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"2554","2555","2556","2557"};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("This is dialog 2.")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this,items[i],Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"2554","2555","2556","2557"};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("This is dialog3.")
                        .setSingleChoiceItems(items,-1,null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ListView listV = ((AlertDialog) dialogInterface).getListView();
                                int j = listV.getCheckedItemPosition();
                                if( j >= 0) {
                                    Toast.makeText(MainActivity.this, items[j], Toast.LENGTH_SHORT).show();
                                }
                                }
                        }).show();
            }
        });

        final ArrayList<Integer> arraylist = new ArrayList<Integer>();

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"2554","2555","2556","2557"};
                final boolean[] isselect = new boolean[4];
                for(int i : arraylist){isselect[i] = true;}
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("This is dialog 4.")
                        .setMultiChoiceItems(items, isselect, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if(b){arraylist.add(i);}
                                else if(arraylist.contains(i)){arraylist.remove(Integer.valueOf(i));}
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String str = "";
                                for(int j : arraylist){str += " " + items[j] + "\n";}
                                if(str.length() > 0){
                                    Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });




    }

}