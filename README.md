# Literalura 📚

Literalura es una aplicación de consola desarrollada en Java que interactúa con la API de Gutendex para gestionar información sobre libros y autores. La aplicación permite realizar búsquedas, listar libros y autores registrados, filtrar autores por año y listar libros por idioma. Los datos se almacenan en una base de datos PostgreSQL.

---

## Características ✨

- **Buscar libros**: Busca un libro por título y almacena su información en la base de datos.
- **Listar libros registrados**: Muestra todos los libros almacenados junto con su autor, idioma y número de descargas.
- **Listar autores registrados**: Muestra los autores registrados junto con sus fechas de nacimiento, fallecimiento y los libros que han escrito.
- **Filtrar autores por año**: Lista autores nacidos después de un año específico.
- **Filtrar libros por idioma**: Lista libros almacenados según el idioma.

---

## Tecnologías utilizadas 🛠️

- **Java**: Lenguaje principal del proyecto.
- **Spring Boot**: Framework para el desarrollo y gestión de dependencias.
- **PostgreSQL**: Base de datos para almacenar libros y autores.
- **Gutendex API**: Fuente de datos para libros y autores.
- **Hibernate/JPA**: Gestión de la persistencia de datos.
- **Maven**: Gestión de dependencias y compilación del proyecto.

---

## Requisitos previos 🚀

1. **Java** (versión 17 o superior) instalado en tu máquina.
2. **Maven** instalado para gestionar dependencias.
3. **PostgreSQL** configurado y accesible con una base de datos creada para el proyecto.
4. Variables de entorno configuradas para conexión a la base de datos:
   - `DB_HOST`: URL de conexión a la base de datos.
   - `DB_USER`: Usuario de la base de datos.
   - `DB_PASSWORD`: Contraseña de la base de datos.
