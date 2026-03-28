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
 * <p>Hibernate genera las consultas SQL de forma transparente a partir
 * de los nombres de los métodos (Spring Data Query Derivation).</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos cuyo nombre contenga el texto dado (búsqueda insensible
     * a mayúsculas/minúsculas).
     *
     * @param nombre texto a buscar en el nombre del producto
     * @return lista de productos que coinciden con la búsqueda
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene todos los productos activos ordenados por nombre ascendente.
     *
     * @return lista de productos activos
     */
    List<Producto> findByActivoTrueOrderByNombreAsc();

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
     * @return lista de productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
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
