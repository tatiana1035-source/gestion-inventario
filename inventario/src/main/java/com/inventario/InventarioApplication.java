package com.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Sistema de Gestión de Inventario.
 *
 * <p>Esta clase arranca la aplicación Spring Boot, la cual configura
 * automáticamente el contexto de Spring, Hibernate/JPA y el servidor
 * web embebido (Tomcat).</p>
 *
 * <p>Tecnologías utilizadas:</p>
 * <ul>
 *   <li>Spring Boot 3 – configuración automática y servidor embebido</li>
 *   <li>Spring MVC – manejo de peticiones HTTP y controladores</li>
 *   <li>Hibernate / JPA – mapeo objeto-relacional (ORM)</li>
 *   <li>Thymeleaf – motor de plantillas para vistas HTML</li>
 *   <li>MySQL – base de datos relacional</li>
 * </ul>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@SpringBootApplication
public class InventarioApplication {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos (no requeridos)
     */
    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }
}
