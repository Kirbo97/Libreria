package com.aluracursos.screenmatch.model;

import jakarta.persistence.*; //libreria de las anotaciones

import java.util.List;

@Entity  //indico que reconosca el contenido como una entidad de la base de datos
@Table(name = "libros") //indico que el nombre de la tabla de la base de datos va a ser otro

public class Libro {
    // Cuando una anotacion esta antes de una V, este actuara como una configuracion de atributo de una BD usando solo a la V que le sigue.
    @Id   //indico que la V con el nombre de esta anotacion va a ser un identificador de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) //indico que esta anotacion va a ser auto incrementable
    private Long Id; //creo la variable que servira como atributo de la tabla
    @Column(unique = true) // esta anotacion es para que solo registre titulos unicos
    private String titulo;
    private Integer descargas;
    private List<String> idiomas;
    @ManyToOne  // esta anotacion convierte esta variable en un atributo con relacion, este atributo recibira la coneccion de la relacion
    private Autor autores;

    public Libro(){} //constructor predeterminado

    public Libro(DatosLibros datosLibro){
        this.titulo = datosLibro.titulo();
        this.descargas = datosLibro.numeroDeDescargas();
        this.idiomas = datosLibro.idiomas();
    }

    @Override
    public String toString() {
        return  "\n************ Libro Encontrado ************" +  '\n' +
                " Titulo = " + titulo +  '\n' +
                " Autor = " + autores.getNombre()+  '\n' +
                " Idioma = " + idiomas +  '\n' +
                " Numero de descargas = " + descargas +  '\n' +
                "*******************************************";
    }

    public void setAutores(Autor autores) { this.autores = autores; }
    public Autor getAutores() { return autores; }

    public Long getId() { return Id; }
    public void setId(Long id) { Id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getDescargas() { return descargas;}
    public void setDescargas(Integer descargas) { this.descargas = descargas; }

    public List<String> getIdiomas() { return idiomas; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
}
