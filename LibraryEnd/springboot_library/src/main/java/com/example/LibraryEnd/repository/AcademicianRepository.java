package com.example.LibraryEnd.repository;


import com.example.LibraryEnd.personel.Academician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AcademicianRepository extends JpaRepository<Academician, Long> {

}