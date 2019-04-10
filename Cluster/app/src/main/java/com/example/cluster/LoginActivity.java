package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPwd;
    private FirebaseAuth auth;
    private Button btnSignUp, btnLogin, btnPwdReset;
    private final int MIN_PWD_LEN = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For checking and set authentication
        //Firebase makes this really easy
        auth = FirebaseAuth.getInstance();

        // if we're already logged in go to the main activity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPwd = (EditText) findViewById(R.id.pwd);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnPwdReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        // make sure log in button tries to log user in
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String pwd = inputPwd.getText().toString();

                // we're using a lot of toasts here, we should replace them in our second sprint.
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.missing_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), R.string.missing_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }

                // pwd too short--I think this is a firebase default? Not sure.
                if (pwd.length() < MIN_PWD_LEN) {
                    inputPwd.setError(getString(R.string.minimum_pwd));
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Handle a failed login
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.access_denied), Toast.LENGTH_LONG).show();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

        // try to sign up user who wants to sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.missing_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.missing_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < MIN_PWD_LEN) {
                    Toast.makeText(getApplicationContext(), R.string.minimum_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // On a failure show a message and the exception
                                // This can probably happen if the user tries to input a duplicate account?
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "" + R.string.sign_up_fail + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                // On a success, just move to the main activity
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}