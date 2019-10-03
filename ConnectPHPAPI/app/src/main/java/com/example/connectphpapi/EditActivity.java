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
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    private static String IP = Config.IP;
    private static String PATH = "edit.php";
    private static final String URL_UPDATE = "http://" + IP + "/apiV1/" + PATH;
    private EditText e_name, e_phone, e_faculty, e_major, e_username, e_password;
    private Button b_edit;
    private String i_name, i_phone, i_faculty, i_major, i_username, i_password, std_id;

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
        std_id = getIntent().getStringExtra("std_id");
        ShowData();
        b_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataInString();
                if(TextUtils.isEmpty(i_name)){
                    Toast.makeText(EditActivity.this,"กรุณาป้อนชื่อนามสกุล",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(i_username)){
                    Toast.makeText(EditActivity.this,"กรุณาป้อน username",Toast.LENGTH_LONG).show();
                    return;
                }
                UpdateData();
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

    private void ShowData() {
        String URL = "http://"+IP+"/apiV1/ed.php?std_id="+std_id;
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...");
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i = 0;i<jsonArray.length();i++){
                                e_name.setText(jsonArray.getJSONObject(i).getString("std_name"));
                                e_phone.setText(jsonArray.getJSONObject(i).getString("std_phone"));
                                e_faculty.setText(jsonArray.getJSONObject(i).getString("std_faculty"));
                                e_major.setText(jsonArray.getJSONObject(i).getString("std_major"));
                                e_username.setText(jsonArray.getJSONObject(i).getString("username"));
                                e_password.setText(jsonArray.getJSONObject(i).getString("password"));
                                progressDialog.dismiss();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Error JON\n"+e,Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onError",error.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"เกิดข้อผิดพลาด"+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void UpdateData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final ProgressDialog progressDialog = ProgressDialog.show(EditActivity.this, "Loading...", "Please wait...");
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE,
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
                        Intent intent = new Intent(getApplicationContext(),ShowRegisterActivity.class);
                        startActivity(intent);
                        finish();
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
                params.put("std_id",std_id);
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
