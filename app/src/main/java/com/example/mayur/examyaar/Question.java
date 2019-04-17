package com.example.mayur.examyaar;

public class Question {
    public String question;
    public String choice1;
    public String choice2;
    public String choice3;
    public String choice4;
    public String answer;

    public Question() {
        //Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question(String question, String choice1, String choice2, String choice3, String choice4, String answer) {
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answer = answer;
    }
}
