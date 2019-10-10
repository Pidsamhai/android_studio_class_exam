package com.example.giftshop;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button b_save;
    private EditText e_product_name, e_product_detail, e_product_tel;
    private FirebaseAuth firebaseAuth;
    //private FloatingActionButton f_add_product;
    private FirebaseUser firebaseUser;
    private String TAG = "AddProductActivity";
    private AlertDialog builder;
    private ImageView pic_img_btn;
    private LinearLayout img_layout;
    private Uri fileUri;
    private RelativeLayout pick_img_layout;
    private UploadTask uploadTask;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ArrayList<Uri> fileUri_list;
    public static final int PICKFILE_RESULT_CODE = 1;
    private String file_extention;

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
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");

        b_save = findViewById(R.id.b_save);
        e_product_name = findViewById(R.id.e_product_name);
        e_product_detail = findViewById(R.id.e_product_detail);
        e_product_tel = findViewById(R.id.e_product_tel);
        pic_img_btn = findViewById(R.id.pick_img_btn);
        img_layout = findViewById(R.id.img_layout);
        fileUri_list = new ArrayList<>();
        pick_img_layout = findViewById(R.id.pick_img_layout);
        //pic_img = findViewById(R.id.pick_img);

        e_product_tel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(AddProductActivity.this)
                .setTitle("Add product...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        pic_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_PICK);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });


        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadImg();
            }
        });


    }


    protected void upLoadImg() {
        builder.show();
        final String unique_id = UUID.randomUUID().toString();
        final String product_id = UUID.randomUUID().toString();
        final StorageReference fileUploadPath = storageReference.child("product/" + unique_id + "." + file_extention);
        uploadTask = fileUploadPath.putFile(fileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Map<String, Object> productObj = new HashMap<>();
                productObj.put("u_id", firebaseUser.getUid());
                productObj.put("product_id", product_id);
                productObj.put("name", e_product_name.getText().toString());
                productObj.put("description", e_product_detail.getText().toString());
                productObj.put("tel", e_product_tel.getText().toString());
                productObj.put("picture", unique_id + "." + file_extention);
                db.collection("product")
                        .add(productObj)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                builder.dismiss();
                                finish();
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        builder.dismiss();
                        finish();
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e);
            }
        });
    }

    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    file_extention = getfileExtension(fileUri);
                    Log.e(TAG, "onActivityResult Extension : " + file_extention);
                    final ImageView imageView = new ImageView(AddProductActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                    lp.setMargins(10, 0, 10, 0);
                    imageView.setLayoutParams(lp);
                    imageView.setBackground(getResources().getDrawable(R.drawable.img_border));
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            new AlertDialog.Builder(AddProductActivity.this)
                                    .setTitle("Remove image?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            imageView.setVisibility(View.GONE);
                                            pic_img_btn.setImageResource(R.drawable.ic_insert_photo_24dp);
                                            pic_img_btn.setEnabled(true);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                            return false;
                        }
                    });
                    Glide.with(AddProductActivity.this)
                            .load(fileUri)
                            .into(imageView);
                    if (fileUri != null){
                        pic_img_btn.setEnabled(false);
                        pic_img_btn.setImageResource(0);
                        pick_img_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
                    }
                    img_layout.addView(imageView);
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
