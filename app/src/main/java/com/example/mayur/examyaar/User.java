package com.example.mayur.examyaar;

public class User {
    public String Email;
    public String FirstName;
    public String LastName;
    public String DOB;

    public User() {
        //Default Constructor for Firebase Reference
    }

    public User(String email, String firstName, String lastName, String DOB) {
        this.Email = email;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.DOB = DOB;
    }
}
