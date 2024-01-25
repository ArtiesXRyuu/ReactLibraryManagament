package com.example.LibraryEnd.personel;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
public class Academician extends User {
    private int academicianNumber;

    public Academician(String firstName, String lastName, String email,Date dateOfBirth,String password, int maxBook, int academicianNumber) {
        super(firstName,lastName, "A", dateOfBirth,password,maxBook,academicianNumber);

        this.academicianNumber = academicianNumber;
    }


}