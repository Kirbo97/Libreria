package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Libro;
import com.aluracursos.screenmatch.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro,Long> {

    List<Libro> findByTitulo(String nomblib);

    List<Libro> findBy();

    @Query("SELECT l FROM Libro l WHERE l.idiomas ILIKE %:idioma%")
    List<Libro> busquedaIdioma(String idioma);
}