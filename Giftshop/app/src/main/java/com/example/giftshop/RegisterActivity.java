package com.example.giftshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Button b_save;
    private TextInputEditText e_email,e_password,e_confirm_password;
    private TextInputLayout l_email,l_password,l_confirm_password;
    private FirebaseAuth firebaseAuth;
    private AlertDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        //bind view id
        e_email = findViewById(R.id.e_email);
        e_password = findViewById(R.id.e_password);
        e_confirm_password = findViewById(R.id.e_confirm_password);
        l_email = findViewById(R.id.email);
        l_password = findViewById(R.id.password);
        l_confirm_password = findViewById(R.id.confirm_password);
        b_save = findViewById(R.id.b_save);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("Loading...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();


        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
                firebaseAuth.createUserWithEmailAndPassword(Objects.requireNonNull(e_email.getText()).toString(), Objects.requireNonNull(e_password.getText()).toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getResult()).toString(),Toast.LENGTH_LONG).show();
                                builder.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });


        //validate email
        e_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Is_Valid_Email(e_email);
            }
            private void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString().isEmpty()) {
                    l_email.setError("Email is required");
                } else if (!isEmailValid(edt.getText().toString())) {
                    l_email.setError("Email is required");
                } else {
                    l_email.setErrorEnabled(false);
                }
            }
            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches();
            }
        });


        e_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                contpassword(e_password,e_confirm_password);

            }
            private void contpassword(EditText edt,EditText c_edt){
                if(!edt.getText().toString().isEmpty()){
                    if(edt.getText().toString().length() >= 6){
                        l_password.setErrorEnabled(false);
                    }else if (edt.getText().toString().length() > 0){
                        l_password.setError("Password minimum 6 characters");
                    }
                }else {
                    l_password.setError("Password is required");
                }

                if(!c_edt.getText().toString().equals(edt.getText().toString())){
                    l_confirm_password.setError("Password not match");
                }else{
                    l_confirm_password.setErrorEnabled(false);
                }
            }
        });

        e_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                check_match(e_password,e_confirm_password);
            }

            private void check_match(EditText edt,EditText c_edt){
                if(!c_edt.getText().toString().equals(edt.getText().toString())){
                    l_confirm_password.setError("Password not match");
                }else{
                    l_confirm_password.setErrorEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
