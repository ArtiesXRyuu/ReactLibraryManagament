package com.example.LibraryEnd.entity;


import lombok.Data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "checkout")
@Data
public class Checkout {

    public Checkout() {}

    public Checkout(String userEmail, String checkoutDate, String returnDate, Long bookId) {
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnDate = returnDate;
        this.bookId = bookId;
    }

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Setter
    @Getter
    @Column(name = "checkout_date")
    private String checkoutDate;

    @Getter
    @Setter
    @Column(name = "return_date")
    private String returnDate;

    @Setter
    @Getter
    @Column(name = "book_id")
    private Long bookId;

}