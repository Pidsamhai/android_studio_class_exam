package com.example.giftshop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private AlertDialog builder;
    private GoogleSignInClient googleSignInClient;
    private Integer RC_SIGN_IN = 7;
    private GoogleSignInOptions gso;
    private CallbackManager callbackManager;
    private String valid_email;
    LoginButton loginButton;
    private static final String TAG = "MainActivity";

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
        callbackManager = CallbackManager.Factory.create();

        final Button b_login = findViewById(R.id.b_login);
        final Button b_google_login = findViewById(R.id.b_google_login);
        final EditText email = findViewById(R.id.e_email);
        final EditText password = findViewById(R.id.e_password);
        final TextInputLayout l_email = findViewById(R.id.email);
        final TextInputLayout l_password = findViewById(R.id.password);
        final Button b_registetr = findViewById(R.id.b_register);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Loading...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();


        loginButton = findViewById(R.id.b_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });


        b_registetr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });




        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Is_Valid_Email(email);
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

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                contpassword(password);
            }
            private void contpassword(EditText edt){
                if(!edt.getText().toString().isEmpty()){
                    if(edt.getText().toString().length() >= 6){
                        l_password.setErrorEnabled(false);
                    }else if (edt.getText().toString().length() > 0){
                        l_password.setError("Password must 6 character");
                    }
                }else {
                    l_password.setError("Password is required");
                }
            }
        });


        b_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(email.getText().toString().trim())) {
                    l_email.setError("Email is required");
                    return;
                }else if(TextUtils.isEmpty(password.getText().toString().trim())){
                    l_password.setError("Password is required");
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            checkUser();
                        } else {
                            LoginManager.getInstance().logOut();
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            checkUser();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("MainActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("MainActivity", "signInWithCredential:success");
                            checkUser();
                        } else {
                            Log.e("MainActivity", "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("Result", "onActivityResult: " + requestCode);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("On activity Result Main", "Google sign in failed", e);
            }
        }
    }

    private void checkUser() {
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
                    finish();
                }
            }, 1000);

        } else {
            builder.dismiss();
            Log.e(TAG, "checkUser: NoUser");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUser();
    }
}
