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
        var menu1 = """
                    \n************************************************
                    *                   M E N U                    *
                    ************************************************
                    *  1 - Buscar libro por titulo.                *
                    *  2 - Listar libros registrados.              *
                    *  3 - Listar autores registrados.             *
                    *  4 - Listar autores de un determinado año.   *
                    *  5 - Listar libros por idioma.               *
                    *  0 - Salir                                   *
                    ************************************************""";

        while (opcion!=0) {

            System.out.println("\n" + menu1);
            System.out.print("\nEscoja una opcion: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            } catch (Exception e) {
                System.out.println("Error desconocido" + e);
            }

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
                    System.out.println("\nCerrando la aplicación...\n");
                    break;
                default:
                    System.out.println("\nOpción inválida, porfavor escoja una opcion del menu.");
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

        if(libroEncon.isPresent()){//Si encontro el libro en la web
            DatosLibros primerLib=datosBusqueda.resultados().get(0);
            Libro libroActual=new Libro(primerLib);

            DatosAutor autorBuscado= primerLib.datosdelautor().get(0);
            Autor autorActual=new Autor(autorBuscado);

            List<Autor> todosAutor = repositorioAutor.findBy();
                //Si esta vacio la BD
            if (todosAutor.size()==0){
                repositorioAutor.save(autorActual);
                libroActual.setAutores(autorActual);
                repositorioLibro.save(libroActual);
                System.out.println(libroActual);
                //Si no esta vacio la BD
            } else {
                validarDatosBD(libroActual, autorActual);
            }

        } else {
            System.out.println("\nLibro no encontrado");
        }
    }

    // Segunda opcion
    public void listarLibroReg(){
        libros = repositorioLibro.findBy();
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
        //El comparator se usa para tener control preciso sobre el orden de clasificación
        autores = repositorioAutor.findBy();
        if(autores.size()!=0){
            for (int i = 0; i < autores.size(); i++) {
                var cont=0;
                List<Libro> todosLibDelAutor = autores.get(i).getLibros();
                System.out.println("\n********** Autor "+(i+1)+" **********" +  '\n' +
                        " Nombre = " + autores.get(i).getNombre() +  '\n' +
                        " Fecha de nacimiento = " + autores.get(i).getFechaDeNacimiento() +  '\n' +
                        " Fecha de fallecimiento = " + autores.get(i).getFechaDeMuerte());
                for (int x = 0; x < todosLibDelAutor.size(); x++) {
                    if (Objects.equals(todosLibDelAutor.get(x).getAutores().getNombre(), autores.get(i).getNombre())){
                        cont++;
                        System.out.println(" Libro "+cont+ " = " + todosLibDelAutor.get(x).getTitulo());
                    }
                }
                System.out.println("*********************************");
            }

        } else {
            System.out.println("\nNo hay Autores registrados que coincidan con el rango");
        }
    }

    // Cuarta opcion
    public void listarAutoresRango(){
        var rango=0;  var Noaut=0;

        System.out.print("\nIngrese el año de nacimiento: ");
        var n1 = teclado.nextInt();

        System.out.print("\nIngrese el año de nacimiento: ");
        var n2 = teclado.nextInt();
        teclado.nextLine();
        List<Autor> filtroAutor= repositorioAutor.findByFechaDeNacimientoGreaterThanEqualAndFechaDeMuerteLessThanEqual(n1, n2);

        if(filtroAutor.size()!=0){
            for (int i = 0; i < filtroAutor.size(); i++) {
                var cont=0;
                List<Libro> filtroLib = filtroAutor.get(i).getLibros();
                System.out.println("\n********** Autor "+(i+1)+" **********" +  '\n' +
                            " Nombre = " + filtroAutor.get(i).getNombre() +  '\n' +
                            " Fecha de nacimiento = " + filtroAutor.get(i).getFechaDeNacimiento() +  '\n' +
                            " Fecha de fallecimiento = " + filtroAutor.get(i).getFechaDeMuerte());
                    for (int x = 0; x < filtroLib.size(); x++) {
                        if (Objects.equals(filtroLib.get(x).getAutores().getNombre(), filtroAutor.get(i).getNombre())){
                            cont++;
                            System.out.println(" Libro "+cont+ " = " + filtroLib.get(x).getTitulo());
                        }
                    }
                    System.out.println("*********************************");
            }

        } else {
            System.out.println("\nNo hay Autores registrados que coincidan con el rango");
        }
    }

    // Quinta opcion
    public void listarLibroIdioma(){
        var menu2 = """
                    \n*********************************
                    *            IDIOMAS            *
                    *********************************
                    *  1) es - Español.             *
                    *  2) en - Ingles.              *
                    *  3) fr - Frances.             *
                    *  4) pt - Portuges.            *
                    *********************************""";
        System.out.println("\n" + menu2);
        System.out.print("\nEscoja una opcion: ");
        var entrada = teclado.nextLine();

        if(Objects.equals(entrada, "1")){ buscarPorIdioma("es"); }
        if(Objects.equals(entrada, "2")){ buscarPorIdioma("en"); }
        if(Objects.equals(entrada, "3")){ buscarPorIdioma("fr"); }
        if(Objects.equals(entrada, "4")){ buscarPorIdioma("pt"); }

        if(!Objects.equals(entrada, "1") && !Objects.equals(entrada, "2") && !Objects.equals(entrada, "3") && !Objects.equals(entrada, "4")){
            System.out.println("\nOpción inválida");
        }
    }

    /////////////////////////////////////////////  select * from autor WHERE (fecha_de_nacimiento >= '1800') and (fecha_de_muerte <= '1889')
    ////////////////////////////////////////////
    public void buscarPorIdioma(String idioma){
        var cont=0;    var Nolib=0;
        libros = repositorioLibro.busquedaIdioma(idioma); //lista de todos los Libros de la BD
        if(libros.size()!=0){
            for (int i = 0; i < libros.size(); i++) {
                    System.out.println("\n*************** Libro "+(i+1)+" ***************" +  '\n' +
                            " Titulo = " + libros.get(i).getTitulo() +  '\n' +
                            " Autor = " + libros.get(i).getAutores().getNombre()+  '\n' +
                            " Idioma = " + libros.get(i).getIdiomas() +  '\n' +
                            " Numero de descargas = " + libros.get(i).getDescargas() +  '\n' +
                            "*******************************************");
            }
        } else {
            System.out.println("\nNo hay Libros registrados con ese idioma.");
        }
    }

    public void validarDatosBD(Libro libroActual, Autor autorActual){
        var bandAutorRep=0;       var bandLibroRep=0;
        // Busca si el titulo de libro se repite
        libros = repositorioLibro.findByTitulo(libroActual.getTitulo());
        if (libros.size()!=0){
            if (Objects.equals(libros.get(0).getAutores().getNombre(), autorActual.getNombre())){ bandLibroRep=1; }
        }

        //Busca si solo el autor se repite
        autores= repositorioAutor.findByNombre(autorActual.getNombre());
        if (autores.size()!=0){
            if(Objects.equals(autores.get(0).getNombre(), autorActual.getNombre())){ bandAutorRep=1; }
        }

        //Si encontro coincidencia con el autor, inyecta los datos del autor viejo con el libro nuevo
        if (bandAutorRep ==1){
            if (bandLibroRep==1){
                System.out.println(libros.get(0));
            } else {
                libroActual.setAutores(autores.get(0));
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


}
