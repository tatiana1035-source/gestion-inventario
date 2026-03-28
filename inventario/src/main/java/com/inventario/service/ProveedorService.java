package com.inventario.service;

import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestion de Proveedor.
 *
 * Historias de usuario que implementa este servicio:
 *
 * CP-006 - Como lider de compras, quiero registrar proveedores
 *          para mejorar la trazabilidad de los productos.
 *          Implementado en: guardar(), actualizar()
 *
 * Criterios de aceptacion CP-006:
 * - La informacion ingresada debe ser veridica y verificable.
 * - No se puede duplicar la informacion del proveedor en el sistema.
 * - Se asocian los productos con cada proveedor.
 *
 * Actores: Lider de compras (segun caso de uso CU-006)
 *
 * Patron de capas: Controller -> Service -> Repository -> Base de datos
 *
 * @author Yuli Tatiana Moreno Vasquez
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProveedorService {

    /** Repositorio JPA para acceso a la tabla proveedores. */
    private final ProveedorRepository proveedorRepository;

    /**
     * Obtiene todos los proveedores ordenados alfabeticamente.
     *
     * @return lista de proveedores
     */
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Busca un proveedor por su identificador.
     *
     * @param id identificador del proveedor
     * @return el proveedor encontrado
     * @throws IllegalArgumentException si no existe
     */
    @Transactional(readOnly = true)
    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro el proveedor con ID: " + id));
    }

    /**
     * Busca proveedores por nombre.
     *
     * @param nombre texto a buscar en el nombre
     * @return lista de proveedores coincidentes
     */
    @Transactional(readOnly = true)
    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Registra un nuevo proveedor en la base de datos.
     *
     * CP-006 - Flujo normal:
     * 1. El usuario accede al modulo de proveedores.
     * 2. Ingresa la informacion (ID, nombre, contacto).
     * 3. Guarda la informacion en la base de datos.
     * 4. Asocia los productos con el proveedor.
     *
     * CP-006 - Flujo alternativo: si el lider de compras encuentra
     * informacion incompleta, el sistema rechaza el registro
     * (validaciones @NotBlank y @Email en la entidad Proveedor).
     *
     * @param proveedor datos del proveedor a registrar
     * @return el proveedor guardado con su ID asignado
     */
    @Transactional
    public Proveedor guardar(Proveedor proveedor) {
        // CP-006: Guardar la informacion verificada del proveedor
        return proveedorRepository.save(proveedor);
    }

    /**
     * Actualiza los datos de un proveedor existente.
     *
     * CP-006 - PosCondicion: la base de datos queda actualizada
     * con la informacion aprobada del proveedor.
     *
     * @param id        identificador del proveedor
     * @param proveedor nuevos datos
     * @return el proveedor actualizado
     */
    @Transactional
    public Proveedor actualizar(Long id, Proveedor proveedor) {
        // Verificar existencia antes de actualizar
        buscarPorId(id);
        proveedor.setIdProveedor(id);
        return proveedorRepository.save(proveedor);
    }

    /**
     * Elimina un proveedor por su identificador.
     *
     * @param id identificador del proveedor a eliminar
     * @throws IllegalArgumentException si no existe
     */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        proveedorRepository.deleteById(id);
    }
}
