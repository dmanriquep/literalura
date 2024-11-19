package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibrosRepository extends JpaRepository<Libro, Integer> {
    @Query("SELECT l FROM Libro l WHERE l.language ILIKE %:language%")
    List<Libro> findByLanguage(String language);
}
