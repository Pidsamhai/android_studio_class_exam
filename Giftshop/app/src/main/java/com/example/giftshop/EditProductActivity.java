package com.example.giftshop;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProductActivity extends AppCompatActivity {

    private EditText e_product_name, e_product_description, e_product_tel, e_facebook_name, e_facebook_url, e_line_url, e_line_id, e_lat, e_lon, e_product_price;
    private ImageView product_img, pick_img_btn, delete_img_btn;
    private Button b_save;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, tempFile;
    private AlertDialog builder;
    private String product_id, file_extention;
    private Product products;
    private static String TAG = "EditProductActivity";
    private Uri fileUri;
    private String oldImage;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_product);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        product_id = getIntent().getStringExtra(IntentStringHelper.PRUDUCT_ID);

        b_save = findViewById(R.id.b_save);
        e_product_name = findViewById(R.id.e_product_name);
        e_product_description = findViewById(R.id.e_product_detail);
        e_product_tel = findViewById(R.id.e_product_tel);
        e_facebook_name = findViewById(R.id.e_facebook_name);
        e_facebook_url = findViewById(R.id.e_facebook_url);
        e_line_url = findViewById(R.id.e_line_url);
        e_line_id = findViewById(R.id.e_line_id);
        e_lat = findViewById(R.id.e_lat);
        e_lon = findViewById(R.id.e_lon);
        e_product_price = findViewById(R.id.e_product_price);
        delete_img_btn = findViewById(R.id.delete_img_btn);

        product_img = findViewById(R.id.product_img);
        pick_img_btn = findViewById(R.id.pick_img_btn);


        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");


        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(EditProductActivity.this)
                .setTitle(R.string._loading)
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        pick_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_PICK);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "" + R.string.choose_file);
                startActivityForResult(chooseFile, IntentStringHelper.PICKFILE_RESULT_CODE);
            }
        });

        builder.setTitle(R.string._get_information);
        builder.show();
        db.collection("product").document(product_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        products = documentSnapshot.toObject(Product.class);
                        Log.e(TAG, "onSuccess: " + products.getName());
                        oldImage = products.getPicture_url();
                        tempFile = storageReference.child("product/" + products.getPicture());
                        Glide.with(EditProductActivity.this)
                                .load(oldImage)
                                .centerCrop()
                                .into(product_img);
                        e_product_name.setText(products.getName());
                        e_product_description.setText(products.getDescription());
                        e_product_tel.setText(products.getTel());
                        e_product_price.setText(products.getPrice());
                        e_facebook_name.setText(products.getFacebook_name());
                        e_facebook_url.setText(products.getFacebook_url());
                        e_line_url.setText(products.getLine_url());
                        e_lat.setText(products.getLat());
                        e_lon.setText(products.getLon());
                        builder.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                builder.dismiss();
            }
        });


        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e_product_name.getText().toString().trim().isEmpty() || e_product_name.getText() == null) {
                    e_product_name.requestFocus();
                    return;
                }
                if (e_product_price.getText().toString().trim().isEmpty() && e_product_price.getText() == null || e_product_price.getText().toString().trim().length() == 0) {
                    e_product_price.requestFocus();
                    return;
                }
                if (e_product_description.getText().toString().trim().isEmpty() || e_product_description.getText() == null) {
                    e_product_description.requestFocus();
                    return;
                }
                if (e_product_tel.getText().toString().trim().isEmpty() || e_product_tel.getText() == null) {
                    e_product_tel.requestFocus();
                    return;
                }
                uPloadData();
            }
        });

        delete_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.app.AlertDialog.Builder(EditProductActivity.this)
                        .setTitle(R.string._remove_image)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Glide.with(EditProductActivity.this)
                                        .load(oldImage)
                                        .centerCrop()
                                        .into(product_img);
                                delete_img_btn.setVisibility(View.GONE);
                                fileUri = null;
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    protected void uPloadData(){
        final Map<String, Object> new_product = new HashMap<>();
        new_product.put("u_id", products.getU_id());
        new_product.put("product_id", product_id);
        new_product.put("name", e_product_name.getText().toString());
        new_product.put("description", e_product_description.getText().toString());
        new_product.put("tel", e_product_tel.getText().toString());
        new_product.put("lat", e_lat.getText().toString());
        new_product.put("lon", e_lon.getText().toString());
        new_product.put("facebook_name", e_facebook_name.getText().toString());
        new_product.put("facebook_url", e_facebook_url.getText().toString());
        new_product.put("line_url", e_line_url.getText().toString());
        new_product.put("line_id", e_line_id.getText().toString());
        new_product.put("price", e_product_price.getText().toString());
        new_product.put("timestamps", FieldValue.serverTimestamp());
        if (fileUri != null) {
            builder.setTitle(R.string._delete_old_image);
            builder.show();

            tempFile = storageReference.child("product/" + products.getPicture());
            tempFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    builder.dismiss();
                    builder.setTitle(R.string._upload_new_image);
                    builder.show();

                    CompressBitmapTask compressBitmapTask = new CompressBitmapTask(EditProductActivity.this, fileUri, new CompressBitmapTask.onSuccessListener() {
                        @Override
                        public void onSuccess(byte[] img_bitmap) {
                            uploadTask = tempFile.putBytes(img_bitmap);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    builder.setTitle(R.string._update_database);
                                    builder.show();
                                    tempFile.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    new_product.put("picture_url", uri.toString());
                                                    db.collection("product").document(products.getProduct_id())
                                                            .update(new_product)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    builder.dismiss();
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            builder.dismiss();
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            builder.dismiss();
                                            finish();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    builder.dismiss();
                                    finish();
                                }
                            });
                        }
                    }, new CompressBitmapTask.onFailureListener() {
                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    compressBitmapTask.execute();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    builder.dismiss();
                    finish();
                }
            });

        } else {
            builder.setTitle(R.string._update_database);
            builder.show();
            db.collection("product").document(products.getProduct_id())
                    .update(new_product)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            builder.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    builder.dismiss();
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentStringHelper.PICKFILE_RESULT_CODE) {
            if (resultCode == -1) {
                fileUri = data.getData();
                Log.e(TAG, "onActivityResult Extension : " + file_extention);
                delete_img_btn.setVisibility(View.VISIBLE);
                Glide.with(EditProductActivity.this)
                        .load(fileUri)
                        .centerCrop()
                        .into(product_img);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
