package com.example.mayur.examyaar;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnlineTest extends AppCompatActivity {

    private FirebaseDatabase myDatabase;
    private DatabaseReference myDBReference;
    private TextView questionNumberTextView;
    private TextView questionTextView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;
    private Question aQuestionReference;
    private LinearLayout onlineTestLinearLayout;
    private int currentQuestionIndex;
    private int numberofQuestions;
    private int score;
    private Handler handler;
    private Button button[];
    public static final String SUBJECT = "Subject";
    private String strCurrentSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test);

        guessLinearLayouts = new LinearLayout[2];
        guessLinearLayouts[0] = (LinearLayout) findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) findViewById(R.id.row2LinearLayout);
        onlineTestLinearLayout = (LinearLayout) findViewById(R.id.onlineTestLinearLayout);
        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        handler = new Handler();
        currentQuestionIndex = 1;
        score = 0;
        myDatabase = FirebaseDatabase.getInstance();

        button = new Button[4];
        button[0] = (Button) guessLinearLayouts[0].getChildAt(0);
        button[1] = (Button) guessLinearLayouts[0].getChildAt(1);
        button[2] = (Button) guessLinearLayouts[1].getChildAt(0);
        button[3] = (Button) guessLinearLayouts[1].getChildAt(1);

        Bundle currentSubject = getIntent().getExtras();
        strCurrentSubject = currentSubject.getString(SUBJECT);
        Display(currentQuestionIndex);
    }

    private void Display(final int key) {
        enableButtons();
        answerTextView.setText("");

        myDBReference = myDatabase.getReference("Subjects");
        myDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberofQuestions = (int) dataSnapshot.getChildrenCount();
                aQuestionReference = dataSnapshot.child(Integer.toString(key - 1)).getValue(Question.class);
                questionNumberTextView.setText(
                        getString(R.string.question, currentQuestionIndex, numberofQuestions)
                );
                questionTextView.setText(aQuestionReference.question);
                button[0].setText(aQuestionReference.choice1);
                button[1].setText(aQuestionReference.choice2);
                button[2].setText(aQuestionReference.choice3);
                button[3].setText(aQuestionReference.choice4);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("loadQues:onCancelled", databaseError.toString());
            }
        });
    }

    public void onGuessButtonClick(View view) {
        Button guessButton = (Button) view;
        String guess = guessButton.getText().toString();
        if (guess.equals(aQuestionReference.answer)) {
            answerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
            answerTextView.setText("Correct!");
            score++;
            disableButtons();
        } else {
            answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
            answerTextView.setText("Hard Luck!\n Correct Answer is:" + aQuestionReference.answer);
            disableButtons();
        }

        if (currentQuestionIndex < numberofQuestions) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Display(++currentQuestionIndex);
                }
            }, 2000);
        }
        else {
            Intent resultsIntent = new Intent(getApplicationContext(), Results.class);
            resultsIntent.putExtra(Results.KEY_SCORE, score );
            resultsIntent.putExtra(SUBJECT, strCurrentSubject);
            startActivity(resultsIntent);
        }
    }

    private void disableButtons() {
        for (int row = 0;
             row < guessLinearLayouts.length; row++) {
            for (int i = 0; i < guessLinearLayouts[row].getChildCount(); i++)
                guessLinearLayouts[row].getChildAt(i).setEnabled(false);
        }
    }

    private void enableButtons() {
        for (int row = 0; row < guessLinearLayouts.length; row++) {
            for (int i = 0; i < guessLinearLayouts[row].getChildCount(); i++)
                guessLinearLayouts[row].getChildAt(i).setEnabled(true);
        }
    }
}
