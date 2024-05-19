package com.aluracursos.screenmatch.principal;

//Importo las clases para usarlos aqui
import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import com.aluracursos.screenmatch.repository.*;

// importo las herramientas para usarlos
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private LibroRepository repositorioLibro;
    private AutorRepository repositorioAutor;
    private List<Libro> libros;
    private List<Autor> autores;

    //se instancia este repositorio
    public Principal(LibroRepository repolibro, AutorRepository repoautor) {
        this.repositorioLibro = repolibro;
        this.repositorioAutor = repoautor;
    } //inyeccion de dependencia

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu1 = """
                    \n******************* M E N U ********************
                    *  1 - Buscar libro por titulo.                *
                    *  2 - Listar libros registrados.              *
                    *  3 - Listar autores registrados.             *
                    *  4 - Listar autores de un determinado año.   *
                    *  5 - Listar libros por idioma.               *
                    *  0 - Salir                                   *
                    ************************************************""";
            System.out.println("\n" + menu1);
            System.out.print("\nEscoja una opcion: ");
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
                case 2:
                    listarLibroReg();
                    break;
                case 3:
                    listarAutoresReg();
                    break;
                case 4:
                    listarLibroIdioma();
                    break;
                case 5:
                    listarLibroIdioma();
                    break;
                case 0:
                    System.out.println("\nCerrando la aplicación...");
                    break;
                default:
                    System.out.println("\nOpción inválida");
            }
        }

    }

// Primera Opcion
    private void buscarLibroWeb() {
        //Busqueda de libros por nombre
        System.out.print("\nEscribe el nombre del libro que deseas buscar: ");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+"?search=" + tituloLibro.replace(" ","+"));
        DatosResultado datosBusqueda = conversor.obtenerDatos(json, DatosResultado.class);

        Optional<DatosLibros> libroEncon = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if(libroEncon.isPresent()){
            DatosLibros primerLib=datosBusqueda.resultados().get(0);
            Libro libro=new Libro(primerLib);

            DatosAutor autorBuscado= primerLib.datosdelautor().get(0);
            Autor autor=new Autor(autorBuscado);
            Autor autorNuevo = repositorioAutor.save(autor);
            libro.setAutores(autorNuevo);
            repositorioLibro.save(libro);
            System.out.println(libro);
        } else{
            System.out.println("Libro no encontrado");
        }
    }

    // Segunda opcion
    public void listarLibroReg(){
        libros = repositorioLibro.findAll();//recojo el contenido del repositorio y lo guardo a una lista
        //El comparator se usa para tener control preciso sobre el orden de clasificación
        if(libros.stream().findFirst().isPresent()){
            libros.stream().forEach(System.out::println);
        } else {
            System.out.println("No hay libros registrados");
        }
    }

    // Tercera opcion
    public void listarAutoresReg(){
        autores = repositorioAutor.findAll();//recojo el contenido del repositorio y lo guardo a una lista
        //El comparator se usa para tener control preciso sobre el orden de clasificación
        if(autores.stream().findFirst().isPresent()){
            autores.stream().forEach(System.out::println);
        } else {
            System.out.println("No hay Autores registrados");
        }
    }

    // Quinta opcion
    public void listarLibroIdioma(){
        var menu2 = """
                    \n********* IDIOMAS ***********
                    *  es - Español.             *
                    *  en - Ingles.              *
                    *  fr - Frances.             *
                    *  pt - Portuges.            *
                    ******************************""";
        System.out.println("\n" + menu2);
        System.out.print("\nEscoja una opcion: ");
        var entrada = teclado.nextInt();
    }
}
