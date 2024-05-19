package com.aluracursos.screenmatch.principal;

//Importo las clases para usarlos aqui
import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import com.aluracursos.screenmatch.repository.*;

// importo las herramientas para usarlos
import java.util.*;

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
                    \n ************************************************
                     *                   M E N U                    *
                     ************************************************
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
                    listarAutoresRango();
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
        var bandAutorRep=0;       var bandLibroRep=0;
        var posaut =0;            var poslib=0;
        //Busqueda de libros por nombre
        System.out.print("\nEscribe el nombre del libro que deseas buscar: ");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+"?search=" + tituloLibro.replace(" ","+"));
        DatosResultado datosBusqueda = conversor.obtenerDatos(json, DatosResultado.class);

        Optional<DatosLibros> libroEncon = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if(libroEncon.isPresent()){//Si encontro el libro en la web
            DatosLibros primerLib=datosBusqueda.resultados().get(0);
            Libro libroActual=new Libro(primerLib);

            DatosAutor autorBuscado= primerLib.datosdelautor().get(0);
            Autor autorActual=new Autor(autorBuscado);

            autores = repositorioAutor.findAll(); //lista de todos los autores de la BD
            libros = repositorioLibro.findAll();//lista de todos los Libros de la BD
                //Si esta vacio la BD
            if (autores.size()==0){
                repositorioAutor.save(autorActual);
                libroActual.setAutores(autorActual);
                repositorioLibro.save(libroActual);
                System.out.println(libroActual);
                //Si no esta vacio la BD
            } else {
                // Busca si el titulo de libro se repite
                for (int i = 0; i < libros.size(); i++) {
                    if(Objects.equals(libros.get(i).getTitulo(), libroActual.getTitulo()) ){
                        if (Objects.equals(libros.get(i).getAutores().getNombre(), autorActual.getNombre())){
                            bandLibroRep=1;      poslib=i;       i=libros.size();
                        }
                    }
                }
                //Busca si solo el autor se repite
                for (int i = 0; i < autores.size(); i++) {
                    if(Objects.equals(autores.get(i).getNombre(), autorActual.getNombre())){
                        bandAutorRep=1;      posaut=i;       i=autores.size();
                    }
                }
                //Si encontro coincidencia con el autor, inyecta los datos del autor viejo con el libro nuevo
                if (bandAutorRep ==1){
                    if (bandLibroRep==1){
                        System.out.println(libros.get(poslib));
                    } else {
                        libroActual.setAutores(autores.get(posaut));
                        repositorioLibro.save(libroActual);
                        System.out.println(libroActual);
                    }
                }else{ //Si no encontro coincidencia, inyecta los datos del autor nuevo con el libro nuevo
                    Autor datosfinal = repositorioAutor.save(autorActual);
                    libroActual.setAutores(datosfinal);
                    repositorioLibro.save(libroActual);
                    System.out.println(libroActual);
                }
            }

        } else {
            System.out.println("Libro no encontrado");
        }
    }

    // Segunda opcion
    public void listarLibroReg(){
        libros = repositorioLibro.findAll();//lista de todos los Libros de la BD
        if(libros.size()!=0){
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("\n************ Libro "+(i+1)+" ************" +  '\n' +
                        " Titulo = " + libros.get(i).getTitulo() +  '\n' +
                        " Autor = " + libros.get(i).getAutores().getNombre()+  '\n' +
                        " Idioma = " + libros.get(i).getIdiomas() +  '\n' +
                        " Numero de descargas = " + libros.get(i).getDescargas() +  '\n' +
                        "*******************************************");
            }
        } else {
            System.out.println("No hay Libros registrados");
        }
    }

    // Tercera opcion
    public void listarAutoresReg(){
        autores = repositorioAutor.findAll(); //lista de todos los autores de la BD
        libros = repositorioLibro.findAll();//lista de todos los Libros de la BD
        if(autores.size()!=0){
            for (int i = 0; i < autores.size(); i++) {
                var cont=0;
                System.out.println("\n********** Autor "+(i+1)+" **********" +  '\n' +
                        " Nombre = " + autores.get(i).getNombre() +  '\n' +
                        " Fecha de nacimiento = " + autores.get(i).getFechaDeNacimiento() +  '\n' +
                        " Fecha de fallecimiento = " + autores.get(i).getFechaDeMuerte());
                for (int x = 0; x < libros.size(); x++) {
                    if (Objects.equals(libros.get(x).getAutores().getNombre(), autores.get(i).getNombre())){
                        cont++;
                        System.out.println(" Libro "+cont+ " = " + libros.get(x).getTitulo());
                    }
                }
                System.out.println("*********************************");
            }
        } else {
            System.out.println("No hay Autores registrados");
        }
    }

    // Cuarta opcion
    public void listarAutoresRango(){
        autores = repositorioAutor.findAll(); //lista de todos los autores de la BD
        libros = repositorioLibro.findAll();//lista de todos los Libros de la BD
        if(autores.size()!=0){
            System.out.print("\nIngrese un año de nacimiento: ");
            var n1 = teclado.nextInt();
            System.out.print("\nIngrese un año de fallecimiento: ");
            var n2 = teclado.nextInt();
            var rango=0;

            for (int i = 0; i < autores.size(); i++) { //Busca todos los autores de la BD
                var cont=0;
                //Verifica si el rango coincide con el autor
                if ((autores.get(i).getFechaDeNacimiento() >= n1) && (autores.get(i).getFechaDeMuerte() <= n2)){
                    rango++;
                    System.out.println("\n********** Autor "+rango+" **********" +  '\n' +
                            " Nombre = " + autores.get(i).getNombre() +  '\n' +
                            " Fecha de nacimiento = " + autores.get(i).getFechaDeNacimiento() +  '\n' +
                            " Fecha de fallecimiento = " + autores.get(i).getFechaDeMuerte());
                    for (int x = 0; x < libros.size(); x++) { //Busca todos los libros del autor
                        if (Objects.equals(libros.get(x).getAutores().getNombre(), autores.get(i).getNombre())){
                            cont++;
                            System.out.println(" Libro "+cont+ " = " + libros.get(x).getTitulo());
                        }
                    }
                    System.out.println("*********************************");
                }
            }
        } else {
            System.out.println("No hay Autores registrados");
        }
    }

    // Quinta opcion
    public void listarLibroIdioma(){
        libros = repositorioLibro.findAll();//lista de todos los Libros de la BD
        var entrada = 1;
        var menu2 = """
                    \n********* IDIOMAS ***********
                    *  1) es - Español.          *
                    *  2) en - Ingles.           *
                    *  3) fr - Frances.          *
                    *  4) pt - Portuges.         *
                    ******************************""";

        while (entrada < 4 && entrada > 0) {
            System.out.println("\n" + menu2);
            System.out.print("\nEscoja una opcion: ");
            entrada = teclado.nextInt();

            if (entrada > 4){ System.out.println("\nOpción inválida"); }
            if (entrada == 1){ busquedaIdioma("es"); entrada=5; }
            if (entrada == 2){ busquedaIdioma("en"); entrada=5; }
            if (entrada == 1){ busquedaIdioma("fr"); entrada=5; }
            if (entrada == 2){ busquedaIdioma("pt"); entrada=5; }
        }

    }

///////////////////////////////////////////////////////////////////
    // funcion repetitiva para buscar libros por idioma
    public void busquedaIdioma(String idio){
        var libenc=0;
        if(libros.size()!=0){
            var cont=0;
            for (int i = 0; i < libros.size(); i++) {
                if (libros.get(i).getIdiomas().contains(idio)) {
                    cont++;
                    System.out.println("\n************ Libro "+cont+" ************" +  '\n' +
                            " Titulo = " + libros.get(i).getTitulo() +  '\n' +
                            " Autor = " + libros.get(i).getAutores().getNombre()+  '\n' +
                            " Idioma = " + libros.get(i).getIdiomas() +  '\n' +
                            " Numero de descargas = " + libros.get(i).getDescargas() +  '\n' +
                            "*******************************************");
                }else {
                    libenc++;
                }
            }

            if(libenc!=0){ System.out.println("\nNo hay Libros con ese idioma registrado"); }
        } else {
            System.out.println("\nNo hay Libros registrados");
        }
    }

}
