package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Datos;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibrosRepository;
import com.aluracursos.literalura.service.ConsultaAPI;
import com.aluracursos.literalura.service.ConvertirDatos;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final LibrosRepository librosRepository;
    private final AutorRepository autorRepository;
    private final Scanner teclado = new Scanner(System.in);
    private List<Libro> libros;
    private List<Autor> autores;
    private final ConsultaAPI consulta = new ConsultaAPI();
    private final ConvertirDatos convertirDatos = new ConvertirDatos();
    private static final String url = "https://gutendex.com/books/";

    public Principal(LibrosRepository librosRepository, AutorRepository autorRepository) {
        this.librosRepository = librosRepository;
        this.autorRepository = autorRepository;
    }

    public void iniciarPrograma() {
        int eleccion = 0;
        while (true) {
            System.out.println("");
            System.out.println("""
                     Elija la opción a través del número:
                     **********************************
                     1: Buscar libro por título
                     2: Listar libros registrados
                     3: Listar autores registrados
                     4: Listar autores vivos por año
                     5: Listar libros por idiomas.
                     **********************************
                     0: Salir.
                    
                    """);
            System.out.print(" : ");
            eleccion = teclado.nextInt();
            teclado.nextLine();
            switch (eleccion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    consultarRegistrados();
                    break;
                case 3:
                    consultarAutoresRegistrados();
                    break;
                case 4:
                    ConsultarFiltroFecha();
                    break;
                case 5:
                    ConsultarIdiomaLibro();
                    break;
                case 0:
                    System.out.println("Has cerrado la app");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Elección no valida");
                    teclado.nextLine();
                    break;
            }
        }
    }

    private Datos buscarDatosLibro() {
        String libroBuscar = "";
        System.out.println("""
                Ingrese el nombre del libro que desea buscar:
                
                """);
        System.out.print(": ");
        libroBuscar = teclado.nextLine();
        String finalName = url + "?search=" + libroBuscar.toLowerCase().replace(" ", "%20");
        var json = consulta.Consultar(finalName);
        Datos datoslibro = convertirDatos.getData(json, Datos.class);
        return datoslibro;
    }

    private void buscarLibro() {
        Datos datosLibro = buscarDatosLibro();
        if (datosLibro.results().isEmpty()) {
            System.out.println("No hay resultados");
            System.out.println("Presione Enter para continuar");
            teclado.nextLine();
        } else {
            // Crear instancias de Libro y Autor basados en los datos obtenidos
            Libro libro = new Libro(datosLibro.results().get(0));
            libro.setTitle(datosLibro.results().get(0).title().length() > 240
                    ? datosLibro.results().get(0).title().substring(0, 240)
                    : datosLibro.results().get(0).title());
            Autor autor = new Autor(datosLibro.results().get(0).authors().get(0));

            try {
                // Verificar si el autor ya existe en la base de datos
                Autor autorExistente = autorRepository.findByName(autor.getName()).orElse(null);

                if (autorExistente != null) {
                    // Si el autor existe, reutilizarlo
                    autor = autorExistente;
                } else {
                    // Si no existe, guardarlo primero
                    autorRepository.save(autor);
                }

                // Asignar el autor al libro y guardar el libro
                libro.setAutor(autor);
                librosRepository.save(libro);

                // Mostrar los resultados
                System.out.println("=".repeat(10) + " Resultados de la búsqueda " + "=".repeat(10));
                System.out.printf("""
                    Titulo: %s
                    Autor: %s
                    Idioma: %s
                    Número de descargas: %s
                    %n""", libro.getTitle(), libro.getAutor().getName(),
                        libro.getLanguage(), libro.getDownload_count());
                System.out.println("=".repeat(10) + " Resultados de la búsqueda " + "=".repeat(10));
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el libro o el autor", e);
            }
        }
    }


    private void consultarRegistrados() {
        try {
            libros = librosRepository.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados...");
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } else {
                libros.forEach(l -> {
                    System.out.printf("""
                            Libro: %s
                            Autor: %s
                            Idioma: %s
                            Descargas: %s
                            %n""", l.getTitle(), l.getAutor(), l.getLanguage(), l.getDownload_count().toString());
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void consultarAutoresRegistrados() {
        autores = autorRepository.findAll();
        try {
            if (autores.isEmpty()) {
                System.out.println("No hay autores registrados...");
                System.out.println("Presione Enter para continuar...");
                teclado.nextLine();
            } else {
                autores.forEach(a -> {
                    List<String> titulosLibros = a.getLibros().stream()
                            .map(Libro::getTitle)
                            .toList();
                    System.out.printf("""
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        Libros: %s
                        %n""",
                            a.getName(),
                            a.getBirth_day() != null ? a.getBirth_day().toString() : "No disponible",
                            a.getDeath_day() != null ? a.getDeath_day().toString() : "Actualmente vivo",
                            titulosLibros.isEmpty() ? "No tiene libros registrados" : titulosLibros.toString());
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void ConsultarFiltroFecha() {
        int autor = 0;
        while (autor == 0) {
            System.out.println("Ingrese el año para buscar a sus respectivos autores");
            System.out.print("> ");
            autor = teclado.nextInt();
            if (autor == 0) {
                System.out.println("Ingrese un año para consultar que autores están registrados en este intervalo.");
                teclado.nextLine();
            } else {
                List<Autor> autores = autorRepository.getAuthorByDate(autor);
                autores.forEach(a -> {
                    System.out.printf("""
                            Autor: %s
                            Nacimiento: %s
                            Fallecimiento: %s
                            %n""", a.getName(), a.getBirth_day() != null ? a.getBirth_day().toString() : "No se encuentra fecha de nacimiento", a.getDeath_day() != null ? a.getDeath_day().toString() : "En la actualidad");
                });
            }
        }
    }

    private void ConsultarIdiomaLibro() {
        int lenguaje = 0;
        while (lenguaje == 0) {
            System.out.println("""
                    Eliga el idioma a consultar del libro:
                    1: en - Inglés
                    2: es - Español
                    3: fr - Francés
                    4: de - Alemán
                    5: it - Italiano
                    """);
            System.out.println("=".repeat(10));
            lenguaje = teclado.nextInt();
            switch (lenguaje) {
                case 1:
                    libros = librosRepository.findByLanguage("en");
                    break;
                case 2:
                    libros = librosRepository.findByLanguage("es");
                    break;
                case 3:
                    libros = librosRepository.findByLanguage("fr");
                    break;
                case 4:
                    libros = librosRepository.findByLanguage("de");
                    break;
                case 5:
                    libros = librosRepository.findByLanguage("it");
                    break;
                default:
                    System.out.println("Elección no valida...\nPresione Enter para reintentar.");
                    teclado.nextLine();
                    break;
            }
        }
        if (libros.isEmpty()) {
            System.out.println("No hay libros con el lenguaje seleccionado...");
            teclado.nextLine();
        } else {
            libros.forEach(l -> {
                System.out.printf("""
                    Libro: %s
                    Autor: %s
                    Idioma: %s
                    Descargas: %s
                    %n""", l.getTitle(), l.getAutor(), l.getLanguage(), l.getDownload_count().toString());
            });
            teclado.nextLine();
        }
    }
}
