package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Integer> {
    @Query("SELECT a FROM Autor a WHERE a.birth_day >= :year ORDER BY a.birth_day ASC")
    List<Autor> getAuthorByDate(Integer year);
    @Query("SELECT a FROM Autor a WHERE a.name = :name")
    Optional<Autor> findByName(String name);
}
