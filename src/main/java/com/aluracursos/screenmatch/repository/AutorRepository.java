package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Libro;
import com.aluracursos.screenmatch.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor,Long> {

    List<Autor> findByFechaDeNacimientoGreaterThanEqualAndFechaDeMuerteLessThanEqual(int n1, int n2);

    List<Autor> findByNombre(String nombaut);

    List<Autor> findBy();



}