package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa una Categoría de productos en el inventario.
 *
 * <p>Cada categoría puede agrupar múltiples productos. Por ejemplo:
 * "Electrónica", "Alimentos", "Ropa", etc.</p>
 *
 * <p>Mapeada a la tabla {@code categorias} en MySQL mediante Hibernate/JPA.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Entity
@Table(name = "categorias")
@Data                   // Lombok: genera getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Lombok: constructor vacío requerido por JPA
public class Categoria {

    /** Identificador único de la categoría (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    /**
     * Nombre descriptivo de la categoría.
     * No puede estar vacío y tiene un máximo de 100 caracteres.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    /** Descripción opcional de la categoría. */
    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Lista de productos que pertenecen a esta categoría.
     * Relación uno-a-muchos: una categoría tiene muchos productos.
     * mappedBy indica que Producto es el dueño de la relación.
     */
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;

    /**
     * Constructor con campos obligatorios.
     *
     * @param nombre      nombre de la categoría
     * @param descripcion descripción de la categoría
     */
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
