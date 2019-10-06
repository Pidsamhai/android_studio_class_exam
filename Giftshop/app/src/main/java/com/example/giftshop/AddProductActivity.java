package com.example.giftshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button b_save;
    private EditText e_product_name,e_product_detail,e_product_tel;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton f_add_product;
    private FirebaseUser firebaseUser;
    private String TAG = "AddProductActivity";
    private AlertDialog builder;
    private ImageView pic_img;
    private LinearLayout img_layout;
    private Uri fileUri;
    private HorizontalScrollView pick_img_layout;
    //private List<Uri> fileUri_list;
    private ArrayList<Uri> fileUri_list;
    public static final int PICKFILE_RESULT_CODE = 1;

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
        f_add_product = findViewById(R.id.f_add_product);
        img_layout = findViewById(R.id.img_layout);
        fileUri_list = new ArrayList<>();
        pick_img_layout = findViewById(R.id.pick_img_layout);
        //pic_img = findViewById(R.id.pick_img);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(AddProductActivity.this)
                .setTitle("Add product...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        f_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });

        /*pic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });

         */

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    final ImageView imageView = new ImageView(AddProductActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300,200);
                    imageView.setLayoutParams(lp);
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            new AlertDialog.Builder(AddProductActivity.this)
                                    .setTitle("Remove image?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            imageView.setVisibility(View.GONE);
                                            fileUri_list.remove(fileUri_list.size()-1);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                            Log.e(TAG, "Current Index: " + fileUri_list.size());
                            if(fileUri_list.size() == 0){
                                pick_img_layout.setBackground(getResources().getDrawable(R.drawable.ic_insert_photo_24dp));
                            }else if(fileUri_list.size() < 3){
                                f_add_product.setEnabled(true);
                            }
                            return false;
                        }
                    });
                    Glide.with(AddProductActivity.this)
                            .load(fileUri)
                            .into(imageView);
                    fileUri_list.add(fileUri);
                    //fileUri_list.add(fileUri);
                    if(fileUri_list.size() > 0 ) pick_img_layout.setBackgroundColor(Color.parseColor("#eeeeee"));

                    Log.e(TAG, "Uri Index: " + fileUri_list.size());
                    img_layout.addView(imageView);
                    if(fileUri_list.size() == 3){
                        f_add_product.setEnabled(false);
                    }
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
