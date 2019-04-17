package com.example.mayur.examyaar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";

    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextDOB;

    private FirebaseAuth firebaseAuth;
    private Button button;
    private DatabaseReference mDatabase;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayDate = (EditText) findViewById(R.id.editTextDOB);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Register.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListner,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        editTextPassword = (EditText) findViewById(R.id.editText_signup_password);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editText_signup_Email);
        editTextDOB = (EditText) findViewById(R.id.editTextDOB);
        button = (Button) findViewById(R.id.button_register);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String firstName = editTextFirstName.getText().toString().trim();
                        if (firstName.isEmpty()) {
                            editTextFirstName.setError("Required Field");
                            editTextFirstName.requestFocus();
                            return;
                        }

                        final String lastName = editTextLastName.getText().toString().trim();
                        if (lastName.isEmpty()) {
                            editTextLastName.setError("Required Field");
                            editTextLastName.requestFocus();
                            return;
                        }

                        final String dob = editTextDOB.getText().toString().trim();
                        if (dob.isEmpty()) {
                            editTextDOB.setError("Required Field");
                            editTextDOB.requestFocus();
                            return;
                        }

                        final String email = editTextEmail.getText().toString().trim();
                        if (email.isEmpty()) {
                            editTextEmail.setError("Required Field");
                            editTextEmail.requestFocus();
                            return;
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            editTextEmail.setError("Enter Valid Email Address");
                            editTextEmail.requestFocus();
                            return;
                        }


                        final String password = editTextPassword.getText().toString().trim();
                        if (password.isEmpty()) {
                            editTextPassword.setError("Required Field");
                            editTextPassword.requestFocus();
                            return;
                        } else if (password.length() < 6) {
                            editTextPassword.setError("Password must be atleast 6 characters");
                            editTextPassword.requestFocus();
                            return;
                        }

                        AlertDialog.Builder a_builder;
                        a_builder = new AlertDialog.Builder(Register.this);
                        a_builder.setMessage("Are you Sure of your Information")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    FirebaseUser aUser;

                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            aUser = firebaseAuth.getCurrentUser();
                                                            mDatabase.child("Users").child(aUser.getUid())
                                                                    .setValue(new User(email, firstName, lastName, dob));
                                                            Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(Register.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = a_builder.create();
                        a_builder.setTitle("Confirmation");
                        alert.show();
                    }
                }
        );

    }
}
