package com.b3po.instaclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.b3po.instaclone.instaposthome.InstagramHome;
import com.b3po.instaclone.userprofile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUserActivity extends AppCompatActivity {

    // DECLARE FIELDS
    EditText emailRegister, passwordRegister;
    Button createAccountButton;

    // FIREBASE AUTHENTICATION ID
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // PROGRESS DIALOG
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // ASSIGN IDS
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        createAccountButton = findViewById(R.id.createAccountButton);

        // PROGRESS DIALOG INSTANCE
        mProgressDialog = new ProgressDialog(this);

        // FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // CHECK USER
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Intent moveToHome = new Intent(RegisterUserActivity.this, InstagramHome.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToHome);
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        // CREATE ON CLICK LISTENER
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Creating Account");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

                createUserAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }

    // CREATE USER ACCOUNT
    private void createUserAccount() {

        String emailUser, passUser;

        emailUser = emailRegister.getText().toString().trim();
        passUser = passwordRegister.getText().toString().trim();

        if (!TextUtils.isEmpty(emailUser) && (!TextUtils.isEmpty(passUser))) {

            mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterUserActivity.this, "Account created successfully.", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();

                        Intent moveToHome = new Intent(RegisterUserActivity.this, InstagramHome.class);
                        moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveToHome);
                    }
                    else {
                        Toast.makeText(RegisterUserActivity.this, "Account creation failed.", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }
}
