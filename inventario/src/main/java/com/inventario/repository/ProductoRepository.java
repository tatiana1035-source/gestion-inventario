package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Producto}.
 *
 * <p>Extiende {@link JpaRepository}, lo cual provee automáticamente
 * los métodos CRUD estándar: save(), findById(), findAll(), deleteById(), etc.</p>
 *
 * <p>Se usan consultas JPQL con LEFT JOIN FETCH para cargar las relaciones
 * LAZY (categoria y proveedor) en la misma consulta, evitando el
 * LazyInitializationException al acceder a ellas en las vistas Thymeleaf.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.1.0
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Obtiene todos los productos activos ordenados por nombre ascendente.
     * Usa JOIN FETCH para cargar categoría y proveedor en la misma consulta.
     *
     * CP-008 - Las alertas de stock bajo se calculan en memoria con isStockBajo().
     *
     * @return lista de productos activos con sus relaciones cargadas
     */
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.activo = true ORDER BY p.nombre ASC")
    List<Producto> findByActivoTrueOrderByNombreAsc();

    /**
     * Busca productos cuyo nombre contenga el texto dado (búsqueda insensible
     * a mayúsculas/minúsculas).
     * Usa JOIN FETCH para cargar categoría y proveedor en la misma consulta.
     *
     * CP-004 - Flujo normal paso 3: el sistema muestra la información solicitada.
     *
     * @param nombre texto a buscar en el nombre del producto
     * @return lista de productos que coinciden con la búsqueda
     */
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor " +
       "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    /**
     * Busca un producto por su código único (SKU).
     *
     * @param codigoProducto código del producto
     * @return el producto encontrado, o vacío si no existe
     */
    Optional<Producto> findByCodigoProducto(String codigoProducto);

    /**
     * Obtiene todos los productos de una categoría específica.
     *
     * @param idCategoria identificador de la categoría
     * @return lista de productos de esa categoría
     */
    List<Producto> findByCategoriaIdCategoria(Long idCategoria);

    /**
     * Consulta JPQL personalizada: retorna productos con stock por debajo
     * del stock mínimo (para alertas de reabastecimiento).
     *
     * CP-008 - Alertas y notificaciones: el sistema controla las cantidades
     * mínimas y genera alerta cuando los productos están en el punto más bajo.
     *
     * @return lista de productos con stock bajo
     */
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();

    /**
     * Cuenta cuántos productos activos hay en una categoría.
     *
     * @param idCategoria identificador de la categoría
     * @return número de productos en esa categoría
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.idCategoria = :idCategoria AND p.activo = true")
    Long countByCategoriaId(@Param("idCategoria") Long idCategoria);
}