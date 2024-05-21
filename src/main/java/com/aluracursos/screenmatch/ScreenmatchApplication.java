package com.aluracursos.screenmatch;

//Importo la libreria propia del Spring
import org.springframework.boot.CommandLineRunner; //implemento la libreria
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired; //libreria para hacer anotaciones de inyeccion de dependencia

//Importo la clase para usarlo aqui
import com.aluracursos.screenmatch.principal.Principal;
import com.aluracursos.screenmatch.repository.*;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	@Autowired  // esta anotacion sirve para hacer o permita una inyeccion de dependencia
	private LibroRepository repositorioLibro;
	@Autowired  // esta anotacion sirve para hacer o permita una inyeccion de dependencia
	private AutorRepository repositorioAutor;
	public static void main(String[] args) { SpringApplication.run(ScreenmatchApplication.class, args); }

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorioLibro, repositorioAutor);
		principal.muestraElMenu();
	}
}
