package com.inventario.repository;

import com.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Categoria}.
 *
 * <p>Provee operaciones CRUD y consultas personalizadas para categorías.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param nombre nombre de la categoría
     * @return la categoría encontrada, o vacío si no existe
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    /**
     * Obtiene todas las categorías ordenadas alfabéticamente.
     *
     * @return lista de categorías ordenadas por nombre
     */
    List<Categoria> findAllByOrderByNombreAsc();

    /**
     * Verifica si ya existe una categoría con ese nombre.
     *
     * @param nombre nombre a verificar
     * @return {@code true} si ya existe
     */
    boolean existsByNombreIgnoreCase(String nombre);
}
