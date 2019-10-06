package com.example.giftshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button b_save;
    private EditText e_product_name,e_product_detail,e_product_tel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String TAG = "AddProductActivity";
    private AlertDialog builder;
    private ImageView pic_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        // init Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Product");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        b_save = findViewById(R.id.b_save);
        e_product_name = findViewById(R.id.e_product_name);
        e_product_detail = findViewById(R.id.e_product_detail);
        e_product_tel = findViewById(R.id.e_product_tel);
        pic_img = findViewById(R.id.pick_img);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(AddProductActivity.this)
                .setTitle("Add product...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        pic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, 2);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }
        });

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
                Map<String,Object> productObj = new HashMap<>();
                productObj.put("u_id",firebaseUser.getUid());
                productObj.put("name",e_product_name.getText().toString());
                productObj.put("description",e_product_detail.getText().toString());
                productObj.put("tel",e_product_tel.getText().toString());
                db.collection("product")
                        .add(productObj)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                builder.dismiss();
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        builder.dismiss();
                        Log.e(TAG, "onFailure: " + e );
                    }
                });
                Log.e(TAG, "onClick: " + productObj);
                builder.dismiss();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
