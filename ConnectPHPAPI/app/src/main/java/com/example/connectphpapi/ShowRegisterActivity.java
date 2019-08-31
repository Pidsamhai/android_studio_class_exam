package com.example.connectphpapi;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowRegisterActivity extends AppCompatActivity {
    private static String IP = "192.168.56.1";
    private static String PATH = "select.php";
    private static final String URL = "http://" + IP + "/apiV1/" + PATH;

    ListView listView;
    List<String> IdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_register);
        listView = findViewById(R.id.list1);

        ShowData();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowRegisterActivity.this)
                        .setTitle("ตัวเลือก")
                        .setMessage("ต้องการเเก้ไข หรือ ลบข้อมูล")
                        .setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder confirm = new AlertDialog.Builder(ShowRegisterActivity.this)
                                        .setTitle("ต้องการคำตอบ")
                                        .setMessage("คุณต้องการลบข้อมูลใช่หรือไม่")
                                        .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Delete(IdList.get(position),position);

                                            }
                                        })
                                        .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                confirm.show();
                            }
                        })
                        .setNegativeButton("เเก้ไข", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNeutralButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alert.show();
                return false;
            }
        });
    }

    public void ShowData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String[] name = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                name[i] = "ชื่อ : " + jsonArray.getJSONObject(i).getString("std_name")
                                        + "\nเบอร์โทร : " + jsonArray.getJSONObject(i).getString("std_phone")
                                        + "\nคณะ : " + jsonArray.getJSONObject(i).getString("std_faculty")
                                        + "\nสาขา : " + jsonArray.getJSONObject(i).getString("std_major");
                                IdList.add(jsonArray.getJSONObject(i).getString("std_id"));
                            }
                            listView.setAdapter(new ArrayAdapter<Object>(getBaseContext(), android.R.layout.simple_list_item_1,name));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.getMessage());
                Toast.makeText(getApplicationContext(), "Error\n" + error, Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void Delete(String std_id, final int position) {
        String url = "http://" + IP + "/apiV1/delete.php/?std_id=" + std_id;
        final ProgressDialog progressDialog = ProgressDialog.show(ShowRegisterActivity.this, "Loading...", "Please wait...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse", response);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        IdList.remove(position); // Fix bug Delete item แก้ ลบข้อมูล ออกแล้วไม่ลบ id ออกจาก list ทำให้ลบตำเเหน่งเดิมไม่ได้
                        ShowData();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onError", error.toString());
                Toast.makeText(getApplicationContext(), "เกิดข้อผิดพลาด", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
        );
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
