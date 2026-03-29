package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad central del sistema: representa un Producto en el inventario.
 *
 * <p>Contiene toda la información relevante de un producto: nombre, precio,
 * stock disponible, categoría y proveedor asociado.</p>
 *
 * <p>Mapeada a la tabla {@code producto} en MySQL mediante Hibernate/JPA.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.1.0
 */
@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
public class Producto {

    /** Identificador único del producto (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    /**
     * Nombre del producto.
     * Campo obligatorio, máximo 150 caracteres.
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /** Descripción detallada del producto. */
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /**
     * Precio unitario del producto.
     * Debe ser un valor positivo mayor a cero.
     * Se usa BigDecimal para evitar errores de redondeo en valores monetarios.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    /**
     * Cantidad actual disponible en inventario.
     * No puede ser negativa.
     * Mapeada a la columna 'cantidad' en la tabla producto.
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "cantidad", nullable = false)
    private Integer stock;

    /**
     * Nivel mínimo de stock antes de generar alerta de reabastecimiento.
     * Por defecto es 5 unidades.
     */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    /**
     * Código o referencia única del producto (SKU).
     * Ejemplo: "PROD-001"
     */
    @Column(name = "codigo_producto", unique = true, length = 50)
    private String codigoProducto;

    /**
     * Fecha y hora en que se registró el producto.
     * Se asigna automáticamente al crear el registro mediante @PrePersist.
     */
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    /** Indica si el producto está activo en el sistema (borrado lógico). */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Categoría a la que pertenece este producto.
     * Relación muchos-a-uno: muchos productos pueden pertenecer a una categoría.
     * FetchType.LAZY: la categoría se carga solo cuando se accede a ella.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    /**
     * Proveedor que suministra este producto.
     * Relación muchos-a-uno: muchos productos pueden venir de un proveedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    /**
     * Callback de JPA: se ejecuta automáticamente antes de insertar el registro.
     * Asigna la fecha y hora actual como fecha de registro.
     */
    @PrePersist
    protected void onCrear() {
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Verifica si el producto tiene stock por debajo del mínimo.
     *
     * @return {@code true} si el stock actual es menor o igual al stock mínimo
     */
    public boolean isStockBajo() {
        return this.stock != null && this.stockMinimo != null
               && this.stock <= this.stockMinimo;
    }
}