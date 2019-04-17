package com.example.mayur.examyaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import  com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button login;
    private int attemptLeft = 3;

    //firebase auth object

    private FirebaseAuth firebaseAuth;

    //progress dialog

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.button_register);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String EMAIL = editTextEmail.getText().toString();
                final String PASSWORD = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(EMAIL)) {
                    editTextEmail.setError("Email Required");
                    editTextEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                    editTextEmail.setError("Please Enter a Valid Email");
                    editTextEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(PASSWORD)) {
                    editTextPassword.setError("Password Required");
                    editTextPassword.requestFocus();
                    return;
                }

                progressDialog.setMessage("Signing In");
                progressDialog.show();
                //Signin with credentials provided
                firebaseAuth.signInWithEmailAndPassword(EMAIL, PASSWORD)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful() == true){
                                    Intent intent = new Intent(LoginActivity.this, SubjectCategories.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(LoginActivity.this,
                                            getString(R.string.Authentication_Error, --attemptLeft),
                                            Toast.LENGTH_LONG).show();
                                    if (attemptLeft == 0)
                                        login.setEnabled(false);
                                }
                            }
                        });

            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }
}
