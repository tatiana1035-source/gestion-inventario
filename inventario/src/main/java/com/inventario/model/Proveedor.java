package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa un Proveedor en el sistema de inventario.
 *
 * <p>Un proveedor es la empresa o persona que suministra los productos.
 * Cada proveedor puede abastecer múltiples productos.</p>
 *
 * <p>Mapeada a la tabla {@code proveedores} en MySQL mediante Hibernate/JPA.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
public class Proveedor {

    /** Identificador único del proveedor (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    /**
     * Nombre o razón social del proveedor.
     * Campo obligatorio, máximo 150 caracteres.
     */
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Número de teléfono de contacto del proveedor.
     * Formato libre, máximo 20 caracteres.
     */
    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Correo electrónico del proveedor.
     * Debe tener formato válido de email.
     */
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    /** Dirección física del proveedor. */
    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    @Column(name = "direccion", length = 255)
    private String direccion;

    /**
     * Lista de productos suministrados por este proveedor.
     * Relación uno-a-muchos: un proveedor abastece muchos productos.
     */
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;

    /**
     * Constructor con campos principales.
     *
     * @param nombre    nombre del proveedor
     * @param telefono  número de contacto
     * @param email     correo electrónico
     * @param direccion dirección del proveedor
     */
    public Proveedor(String nombre, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }
}
