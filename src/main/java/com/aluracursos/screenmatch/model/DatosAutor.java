package com.aluracursos.screenmatch.model;

// la libreria de jackson se implementa añadiendo su codigo en el pom.xml en la seccion de dependencia
// dicho codigo se obtiene de la pagina https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //ignora los campos que no se especificaron aqui
//@JsonAlias es una funcion para dal un alias para indicar que la V lo referencie con el alias

public record DatosAutor(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") Integer fechaDeNacimiento,
        @JsonAlias("death_year")Integer fechaDeMuerte
) { }
