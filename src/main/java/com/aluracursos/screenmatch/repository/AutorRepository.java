package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Autor;
import com.aluracursos.screenmatch.model.DatosAutor;
import com.aluracursos.screenmatch.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {

    Optional<Autor> findByNombre(String nombre);
}