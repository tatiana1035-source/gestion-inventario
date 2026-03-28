package com.inventario.repository;

import com.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Proveedor}.
 *
 * <p>Provee operaciones CRUD y consultas de búsqueda para proveedores.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    /**
     * Busca proveedores cuyo nombre contenga el texto indicado.
     *
     * @param nombre fragmento del nombre a buscar
     * @return lista de proveedores que coinciden
     */
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene todos los proveedores ordenados por nombre.
     *
     * @return lista de proveedores ordenada alfabéticamente
     */
    List<Proveedor> findAllByOrderByNombreAsc();

    /**
     * Busca un proveedor por su correo electrónico.
     *
     * @param email correo del proveedor
     * @return el proveedor encontrado, o vacío si no existe
     */
    java.util.Optional<Proveedor> findByEmailIgnoreCase(String email);
}
