package com.example.giftshop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private AlertDialog builder;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View context = findViewById(R.id.context_mainactivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();

        Button b_login = findViewById(R.id.b_login);
        final EditText email = findViewById(R.id.e_email);
        final EditText password = findViewById(R.id.e_password);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Loading...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();


        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(email.getText().toString().trim()) || TextUtils.isEmpty(password.getText().toString().trim())) {
                    if (TextUtils.isEmpty(email.getText().toString().trim())) {
                        email.setError("Email is required");
                    } else {
                        password.setError("Password is required");
                    }
                    //builder.dismiss();
                    return;
                }

                builder.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final Snackbar snackbar = Snackbar.make(context, "Login Success!", Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();


                        if (task.isSuccessful()) {

                            snackbarView.setBackgroundColor(Color.parseColor("#03fc49"));
                            snackbar.show();
                            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    builder.dismiss();
                                    startActivity(intent);
                                }
                            }, 2000);

                        } else {
                            builder.dismiss();
                            String[] err_msg = task.getException().toString().split(":");
                            snackbarView.setBackgroundColor(Color.parseColor("#fc0303"));
                            snackbar.setDuration(Snackbar.LENGTH_LONG);
                            snackbar.setText(err_msg[1].trim());
                            snackbar.show();
                            Log.e("Tag", task.getException().toString());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please...wait");
        builder.show();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    builder.dismiss();
                    startActivity(intent);
                }
            }, 1000);

        } else {
            builder.dismiss();
        }
    }
}
