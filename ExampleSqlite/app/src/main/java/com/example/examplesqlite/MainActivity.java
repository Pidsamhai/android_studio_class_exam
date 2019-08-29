package com.example.examplesqlite;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.excamplesqlite.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    private DataBaseHelper db;
    private Spinner spinnerCategories;
    private EditText editTextCategory;
    private Button buttonCategory;
    private List<String> listCategories = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // connect data base
        db = new DataBaseHelper(this);
        if (db == null) db = new DataBaseHelper(this);

        listView = findViewById(R.id.todo_listView);
        spinnerCategories = findViewById(R.id.spinner_categories);
        editTextCategory = findViewById(R.id.edit_newcategory);
        buttonCategory = findViewById(R.id.bottom_category);
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCategory = editTextCategory.getText().toString();
                if (newCategory.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter New Category", Toast.LENGTH_LONG).show();
                } else {
                    db.addCategory(new Category(newCategory));
                    prepareData();
                    Toast.makeText(MainActivity.this,"New Category was Successfully added to Database",Toast.LENGTH_LONG).show();
                }
            }
        });
        prepareData();
    }
    public void prepareData(){
        listCategories = db.getAllCategories();
        //Log.e("ERROR", "HELLO");
        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,listCategories);
        listView.setAdapter(adapter);
        spinnerCategories.setAdapter(adapter);
    }
}
