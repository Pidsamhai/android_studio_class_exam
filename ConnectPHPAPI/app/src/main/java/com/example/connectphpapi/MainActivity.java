package com.example.connectphpapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String IP = Config.IP;
    //private static String PATH = "dummy_repeat.php";
    private static String PATH = "insert.php";
    private static String URL = "http://" + IP + "/apiV1/" + PATH;
    private EditText e_name, e_phone, e_faculty, e_major, e_username, e_password;
    private Button b_insert, b_showdata;
    private String i_name, i_phone, i_faculty, i_major, i_username, i_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e_name = findViewById(R.id.e_name);
        e_phone = findViewById(R.id.e_phone);
        e_faculty = findViewById(R.id.e_faculty);
        e_major = findViewById(R.id.e_major);
        e_username = findViewById(R.id.e_username);
        e_password = findViewById(R.id.e_password);
        b_insert = findViewById(R.id.b_insert);
        b_showdata = findViewById(R.id.b_showdata);

        b_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DataInString();

                if (TextUtils.isEmpty(i_name)) {
                    e_name.setError("กรุณาป้อนชื่อนามสกุล");
                    return;
                }
                if (TextUtils.isEmpty(i_username)) {
                    e_username.setError("กรุณาป้อน username");
                    return;
                }
                if (TextUtils.isEmpty(i_password)) {
                    e_username.setError("กรุณาป้อน password");
                    return;
                }

                InsertData();

            }
        });
        b_showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShowRegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void DataInString() {
        i_name = e_name.getText().toString();
        i_phone = e_phone.getText().toString();
        i_faculty = e_faculty.getText().toString();
        i_major = e_major.getText().toString();
        i_username = e_username.getText().toString();
        i_password = e_password.getText().toString();
    }

    private void InsertData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...");
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse", response);
                        e_name.setText("");
                        e_phone.setText("");
                        e_faculty.setText("");
                        e_major.setText("");
                        e_username.setText("");
                        e_password.setText("");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onError", error.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "เกิดข้อผิดพลาด " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()  throws AbstractMethodError {
                Map<String, String> params = new HashMap<>();
                params.put("name", i_name);
                params.put("phone", i_phone);
                params.put("faculty", i_faculty);
                params.put("major", i_major);
                params.put("username", i_username);
                params.put("password", i_password);
                //Log.e("PARAMS", String.valueOf(params));
                return params;
            }
        };
        requestQueue.add(request);
    }
}
