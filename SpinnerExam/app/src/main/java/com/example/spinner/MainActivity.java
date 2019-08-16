package com.example.spinner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> arrList = new ArrayList<String>();
        arrList.add("Mocca");
        arrList.add("Late");
        arrList.add("Arabica");
        arrList.add("Esspresso");
        arrList.add("Capuchino");
        final ArrayAdapter<String> arrAd = new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,arrList);
        Spinner spinner =  findViewById(R.id.spinner1);

        spinner.setAdapter(arrAd);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,"Your Selected : "+arrAd.getItem(i),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this,"Your Selected Empty",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
