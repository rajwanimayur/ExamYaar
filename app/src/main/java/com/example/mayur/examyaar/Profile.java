package com.example.mayur.examyaar;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView dob;
    private ProgressBar progressBar;

    private FirebaseUser aUser;
    private DatabaseReference myDBReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = (TextView) findViewById(R.id.profile_textView_Display_FirstName);
        lastName = (TextView) findViewById(R.id.profile_textView_Display_LastName);
        dob = (TextView) findViewById(R.id.profile_textView_Display_DOB);
        email = (TextView) findViewById(R.id.profile_textView_Display_Email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);

        aUser = FirebaseAuth.getInstance().getCurrentUser();
        if(aUser != null) {
            myDBReference = FirebaseDatabase.getInstance().getReference("Users").child(aUser.getUid());
            myDBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User aUserObject = dataSnapshot.getValue(User.class);
                    firstName.setText(aUserObject.FirstName);
                    lastName.setText(aUserObject.LastName);
                    email.setText(aUserObject.Email);
                    dob.setText(aUserObject.DOB);

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void onChangePassword(View v){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_update_password, null);
        final EditText currentPasswd = (EditText) mView.findViewById(R.id.dialog_editTextCurrentPassword);
        final EditText newPasswd = (EditText) mView.findViewById(R.id.dialog_editTextNewPassword);

        final Button btnUpdate = (Button) mView.findViewById(R.id.dialog_btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthCredential credential =
                        EmailAuthProvider.getCredential(email.getText().toString(), currentPasswd.getText().toString());
                aUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            String strNewPass = newPasswd.getText().toString();
                            aUser.updatePassword(strNewPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else
                                        Toast.makeText(Profile.this, "Error! Can't Update Password", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else
                            Log.d("Error", "Firebase Authentication Failed");
                    }
                });
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
}
