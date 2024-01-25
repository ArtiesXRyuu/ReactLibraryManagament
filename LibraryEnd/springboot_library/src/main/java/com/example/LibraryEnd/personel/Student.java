package com.example.LibraryEnd.personel;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
public class Student extends User {
    private int studentNumber;

    public Student(String firstName, String lastName, String email, Date dateOfBirth, String password,int maxBook, int studentNumber) {
        super(firstName, lastName, "S", dateOfBirth, password, maxBook, studentNumber);
        this.studentNumber = studentNumber;
    }


}