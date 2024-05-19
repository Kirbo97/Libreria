package com.aluracursos.screenmatch.model;

import jakarta.persistence.*;

import java.util.List;
//Importo la libreria para usar y modificar formato de fecha y hora


@Entity  //indico que reconosca el contenido como una entidad de la base de datos
@Table(name = "autor") //indico que el nombre de la tabla de la base de datos va a ser otro

public class Autor {
    // Cuando una anotacion esta antes de una V, este actuara como una configuracion de atributo de una BD usando solo a la V que le sigue.
    @Id   //indico que la V con el nombre de esta anotacion va a ser un identificador de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) //indico que esta anotacion va a ser auto incrementable
    private Long Id;
    private String nombre;
    private Integer fechaDeNacimiento;
    private Integer fechaDeMuerte;
    @OneToMany(mappedBy = "autores", cascade = CascadeType.ALL, fetch = FetchType.EAGER)  // esta anotacion convierte esta variable en un atributo con relacion
    private List<Libro> libros;

    public Autor(){} //constructor predeterminado

    public Autor(DatosAutor autor) {
        this.nombre = autor.nombre();
        this.fechaDeMuerte = autor.fechaDeMuerte();
        this.fechaDeNacimiento = autor.fechaDeNacimiento();
    }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) {
        libros.forEach(e -> e.setAutores(this)); //usado para realizar la busqueda presisa
        this.libros = libros;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String titulo) { this.nombre = titulo; }

    public Integer getFechaDeNacimiento() { return fechaDeNacimiento; }
    public void setFechaDeNacimiento(Integer fechaDeNacimiento) { this.fechaDeNacimiento = fechaDeNacimiento; }

    public Integer getFechaDeMuerte() { return fechaDeMuerte; }
    public void setFechaDeMuerte(Integer fechaDeMuerte) { this.fechaDeMuerte = fechaDeMuerte; }
/*
    @Override
    public String toString() {
        return  "\n******* Autor Encontrado *******" +  '\n' +
                " Nombre = " + nombre +  '\n' +
                " Fecha de nacimiento = " + fechaDeNacimiento +  '\n' +
                " Fecha de fallecimiento = " + fechaDeMuerte +  '\n' +
                " Libro = " + libros +  '\n' +
                "*********************************";
    }*/

    @Override
    public String toString() {
        return  "\n******* Autor Encontrado *******" +  '\n' +
                " Libro = " + libros.get(0).getTitulo() +  '\n' +
                " Nombre = " + nombre +  '\n' +
                " Fecha de nacimiento = " + fechaDeNacimiento +  '\n' +
                " Fecha de fallecimiento = " + fechaDeMuerte +  '\n' +
                "*********************************";
    }
}
