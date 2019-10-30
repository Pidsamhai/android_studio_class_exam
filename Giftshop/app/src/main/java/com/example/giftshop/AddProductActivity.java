package com.example.giftshop;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.giftshop.Helper.IntentStringHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class AddProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button b_save;
    private EditText e_product_name, e_product_detail, e_product_tel, e_facebook_name, e_facebook_url, e_line_url, e_line_id, e_lat, e_lon, e_price;
    private FirebaseAuth firebaseAuth;
    //private FloatingActionButton f_add_product;
    private FirebaseUser firebaseUser;
    private String TAG = "AddProductActivity";
    private AlertDialog builder;
    private ImageView pic_img_btn;
    private ImageView img_layout;
    private Uri fileUri;
    private RelativeLayout pick_img_layout;
    private UploadTask uploadTask;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ArrayList<Uri> fileUri_list;
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
        e_facebook_name = findViewById(R.id.e_facebook_name);
        e_facebook_url = findViewById(R.id.e_facebook_url);
        e_line_url = findViewById(R.id.e_line_url);
        e_line_id = findViewById(R.id.e_line_id);
        e_lat = findViewById(R.id.e_lat);
        e_lon = findViewById(R.id.e_lon);
        e_price = findViewById(R.id.e_product_price);
        pic_img_btn = findViewById(R.id.pick_img_btn);
        img_layout = findViewById(R.id.img_layout1);
        fileUri_list = new ArrayList<>();
        pick_img_layout = findViewById(R.id.pick_img_layout);


        e_product_tel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(AddProductActivity.this)
                .setTitle(R.string.add_product)
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        pic_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_PICK);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "" + R.string.choose_file);
                startActivityForResult(chooseFile, IntentStringHelper.PICKFILE_RESULT_CODE);
            }
        });


        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser.getDisplayName() == null) {
                    Toast.makeText(AddProductActivity.this, R.string._please_add_your_name_before, Toast.LENGTH_LONG).show();
                    return;
                }
                if (fileUri == null) {
                    Toast.makeText(AddProductActivity.this, R.string._please_addimage, Toast.LENGTH_LONG).show();
                    return;
                }
                if (e_product_name.getText().toString().trim().isEmpty() || e_product_name.getText() == null) {
                    e_product_name.requestFocus();
                    return;
                }
                if (e_price.getText().toString().trim().isEmpty() && e_price.getText() == null || e_price.getText().toString().trim().length() == 0) {
                    e_price.requestFocus();
                    return;
                }
                if (e_product_detail.getText().toString().trim().isEmpty() || e_product_detail.getText() == null) {
                    e_product_detail.requestFocus();
                    return;
                }
                if (e_product_tel.getText().toString().trim().isEmpty() || e_product_tel.getText() == null) {
                    e_product_tel.requestFocus();
                    return;
                }

                upLoadImg();
            }
        });


    }

    protected void upLoadImg() {
        Log.e(TAG, "test_upload_task: " + "Init Upload Image");
        builder.show();
        final String unique_id = UUID.randomUUID().toString();
        final String product_id = UUID.randomUUID().toString();
        final StorageReference fileUploadPath = storageReference.child("product/" + unique_id + ".jpg");
        CompressBitmapTask compressBitmapTask = new CompressBitmapTask(this, fileUri, new CompressBitmapTask.onSuccessListener() {
            @Override
            public void onSuccess(byte[] img_bitmap) {
                uploadTask = fileUploadPath.putBytes(img_bitmap);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        fileUploadPath.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Date c = Calendar.getInstance().getTime();
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                        String forematdate = df.format(c);
                                        Log.e(TAG, "onSuccess: Date Time :" + forematdate);
                                        Map<String, Object> productObj = new HashMap<>();
                                        productObj.put("u_id", firebaseUser.getUid());
                                        productObj.put("u_name", firebaseUser.getDisplayName());
                                        productObj.put("u_pic", firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString());
                                        productObj.put("product_id", product_id);
                                        productObj.put("name", e_product_name.getText().toString());
                                        productObj.put("description", e_product_detail.getText().toString());
                                        productObj.put("tel", e_product_tel.getText().toString());
                                        productObj.put("picture", unique_id + ".jpg");
                                        productObj.put("picture_url", uri.toString());
                                        productObj.put("lat", e_lat.getText().toString().trim());
                                        productObj.put("lon", e_lon.getText().toString().trim());
                                        productObj.put("facebook_name", e_facebook_name.getText().toString().trim());
                                        productObj.put("facebook_url", e_facebook_url.getText().toString().trim());
                                        productObj.put("line_url", e_line_url.getText().toString().trim());
                                        productObj.put("line_id", e_line_id.getText().toString().trim());
                                        productObj.put("price", e_price.getText().toString());
                                        productObj.put("timestamps", FieldValue.serverTimestamp());
                                        db.collection("product").document(product_id)
                                                .set(productObj)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        builder.dismiss();
                                                        finish();
                                                        Log.d(TAG, "DocumentSnapshot written with ID: " + product_id);
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
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        builder.dismiss();
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e);
                        builder.dismiss();
                    }
                });
            }
        }, new CompressBitmapTask.onFailureListener() {
            @Override
            public void onFailure(Exception e) {
                builder.dismiss();
                Toast.makeText(AddProductActivity.this,"Error " + e,Toast.LENGTH_LONG).show();
            }
        });
        compressBitmapTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentStringHelper.PICKFILE_RESULT_CODE) {
            if (resultCode == -1) {
                fileUri = data.getData();
                Log.e(TAG, "onActivityResult Extension : " + file_extention);
                img_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(AddProductActivity.this)
                                .setTitle(R.string._remove_image)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        img_layout.setVisibility(View.GONE);
                                        fileUri = null;
                                        pic_img_btn.setImageResource(R.drawable.ic_insert_photo_24dp);
                                        pic_img_btn.setEnabled(true);
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                        return false;
                    }
                });
                Glide.with(AddProductActivity.this)
                        .load(fileUri)
                        .centerCrop()
                        .into(img_layout);
                if (fileUri != null) {
                    pic_img_btn.setEnabled(false);
                    pic_img_btn.setImageResource(0);
                    pick_img_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
                    img_layout.setVisibility(View.VISIBLE);
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
