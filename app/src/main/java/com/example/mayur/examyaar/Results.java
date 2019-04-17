package com.example.mayur.examyaar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Results extends AppCompatActivity {
    private TextView displayScore;
    private TextView bestScore;
    private int currentScore;
    private int currentBestScore;
    private String currentSubject;
    public static final String KEY_SCORE = "Score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        displayScore = (TextView) findViewById(R.id.results_display_score_textView);
        bestScore = (TextView) findViewById(R.id.results_display_bestScore_textView);

        Bundle examScore = getIntent().getExtras();
        currentScore = examScore.getInt(KEY_SCORE);
        currentSubject = examScore.getString(OnlineTest.SUBJECT);
        displayScore.setText(String.valueOf(currentScore));

        addScoreDatabase();
    }

    public void onReset(View view) {
        Intent reset = new Intent(this, OnlineTest.class);
        reset.putExtra(OnlineTest.SUBJECT, currentSubject);
        reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(reset);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this, SubjectCategories.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(back);
    }

    public void onShareButtonClicked(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.results_shareText, currentScore, currentBestScore));
        startActivity(shareIntent);
    }


    private void addScoreDatabase() { //Function to add user score to database
        FirebaseUser aUser = FirebaseAuth.getInstance().getCurrentUser();
        if (aUser != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(aUser.getUid()).child("Scores");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentBestScore = (int) dataSnapshot.child(currentSubject).child("BestScore").getValue(Integer.class);
                    if (currentScore > currentBestScore) {
                        currentBestScore = currentScore;
                        databaseReference.child(currentSubject).child("LastScore").setValue(currentScore);
                        databaseReference.child(currentSubject).child("BestScore").setValue(currentBestScore);
                        Toast.makeText(getApplicationContext(), "You've a new Best Score", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        databaseReference.child(currentSubject).child("LastScore").setValue(currentScore);
                        bestScore.setText(String.valueOf(currentBestScore));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ERROR", databaseError.toString());
                }
            });
        } else {
            Toast.makeText(Results.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }

    }
}
