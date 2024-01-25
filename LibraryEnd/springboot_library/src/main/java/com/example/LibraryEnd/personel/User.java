package com.example.LibraryEnd.personel;

import com.example.LibraryEnd.entity.Book;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;


import java.util.Date;


public class User {
    @Setter
    @Getter
    public String firstName;
    @Setter
    @Getter
    public String lastName;
    @Getter
    @Setter
    private String email;
    @Setter
    @Getter
    private Date dateOfBirth;
    @Setter
    @Getter
    private String password;
    @Setter
    @Getter
    private int maxBook;
    @Setter
    @Getter
    private int numberID;

    private List<Book> books;


    public User(String firstName, String lastName, String email, Date dateOfBirth, String password, int maxBook, int numberID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.maxBook = maxBook;
        this.numberID = numberID;
    }


}
