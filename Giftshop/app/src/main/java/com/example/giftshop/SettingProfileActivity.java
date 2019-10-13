package com.example.giftshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;
import java.util.Random;

public class SettingProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    public static final int PICKFILE_RESULT_CODE = 1;
    private Uri fileUri;
    private EditText e_tel;
    private ImageView profile_pic;
    private UploadTask uploadTask;
    private AlertDialog builder;
    private UserProfileChangeRequest profileChangeRequest;
    private View snackbarView;
    private Snackbar snackbar;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Setting");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final View context = findViewById(R.id.context_setting_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
        final StorageReference fileUploadPath = storageReference.child("profile/" + firebaseUser.getUid() + ".png");

        Button b_save = findViewById(R.id.b_save);

        final EditText e_name = findViewById(R.id.e_name);
        final EditText e_last_name = findViewById(R.id.e_last_name);
        e_tel = findViewById(R.id.e_tel);
        profile_pic = findViewById(R.id.profile_pic);

        // init load setting
        if(firebaseUser.getPhotoUrl() != null){

            Glide.with(SettingProfileActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_pic);
        }else{
            Integer[] avatar = {
                    R.drawable.ic_avatar_1,
                    R.drawable.ic_avatar_2,
                    R.drawable.ic_avatar_3,
                    R.drawable.ic_avatar_4,
                    R.drawable.ic_avatar_5,
                    R.drawable.ic_avatar_6,
                    R.drawable.ic_avatar_7,
                    R.drawable.ic_avatar_8,
                    R.drawable.ic_avatar_9,
            };
            Integer index = new Random().nextInt(9);
            Glide.with(SettingProfileActivity.this)
                    .load(avatar[index])
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_pic);
        }

        if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().isEmpty()){
            Log.e("Dispaly name", "onCreate: " + firebaseUser.getDisplayName());
            //String[] name = firebaseUser.getDisplayName().split(" ");

            //e_name.setText(name[0]);
           // e_last_name.setText(name[1]);
        }

        snackbar = Snackbar.make(context, "Save success!", Snackbar.LENGTH_LONG);
        snackbarView = snackbar.getView();
        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(SettingProfileActivity.this)
                .setTitle("Saving...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
                if (fileUri != null) {
                    uploadTask = fileUploadPath.putFile(fileUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e("Setting", "onSuccess: " + taskSnapshot.getMetadata());
                            fileUploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(e_name.getText().toString() + " " + e_last_name.getText().toString())
                                            .setPhotoUri(uri)
                                            .build();

                                    updateProfile(profileChangeRequest);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            builder.dismiss();
                        }
                    });
                } else {

                    profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(e_name.getText().toString()+" "+e_last_name.getText().toString())
                            .build();

                    updateProfile(profileChangeRequest);

                }
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_PICK);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });

        profile_pic.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                if(fileUri != null){
                    new AlertDialog.Builder(SettingProfileActivity.this)
                            .setTitle("Remove picture profile")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(firebaseUser.getPhotoUrl() == null){
                                        Integer[] avatar = {
                                                R.drawable.ic_avatar_1,
                                                R.drawable.ic_avatar_2,
                                                R.drawable.ic_avatar_3,
                                                R.drawable.ic_avatar_4,
                                                R.drawable.ic_avatar_5,
                                                R.drawable.ic_avatar_6,
                                                R.drawable.ic_avatar_7,
                                                R.drawable.ic_avatar_8,
                                                R.drawable.ic_avatar_9,
                                        };
                                        Integer index = new Random().nextInt(9);
                                        Glide.with(getApplicationContext())
                                                .load(avatar[index])
                                                .centerCrop()
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(profile_pic);
                                        fileUri = null;
                                    }else {
                                        Glide.with(SettingProfileActivity.this)
                                                .load(firebaseUser.getPhotoUrl())
                                                .centerCrop()
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(profile_pic);
                                        fileUri = null;
                                    }

                                }
                            }).setNegativeButton("Cancel",null)
                            .show();
                }else{

                }
                return false;
            }
        });

    }

    void updateProfile(UserProfileChangeRequest profileChangeRequest){
        firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    snackbarView.setBackgroundColor(Color.parseColor("#03fc49"));
                    snackbar.show();
                    builder.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("Run", "run: " + firebaseUser.getPhotoUrl());
                            finish();
                        }
                    }, 1000);

                } else {
                    snackbarView.setBackgroundColor(Color.parseColor("#fc0303"));
                    snackbar.show();
                    builder.dismiss();
                }
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
                    Glide.with(SettingProfileActivity.this)
                            .load(fileUri)
                            .centerCrop()
                            .apply(RequestOptions.circleCropTransform())
                            .into(profile_pic);
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
