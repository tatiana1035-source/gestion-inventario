package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestion de Producto.
 *
 * Historias de usuario que implementa este servicio:
 *
 * CP-001 - Como lider de inventario, quiero registrar nuevos productos
 *          para mantener el stock actualizado.
 *          Implementado en: guardar()
 *
 * CP-002 - Como auxiliar de inventario, quiero actualizar la cantidad
 *          de productos disponibles para reflejar cambios en el stock.
 *          Implementado en: actualizarStock()
 *
 * CP-004 - Como auxiliar de inventario, quiero buscar productos
 *          rapidamente para consultar su informacion.
 *          Implementado en: buscarPorNombre()
 *
 * CP-008 - Como lider de inventario, quiero recibir alertas cuando
 *          un producto alcance el nivel minimo de stock.
 *          Implementado en: obtenerProductosStockBajo()
 *
 * Patron de capas: Controller -> Service -> Repository -> Base de datos
 *
 * @Transactional garantiza que cada operacion de escritura se ejecute
 * dentro de una transaccion de base de datos (Hibernate/JPA).
 *
 * @author Yuli Tatiana Moreno Vasquez
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    /** Repositorio JPA para acceso a la tabla productos. */
    private final ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos activos del inventario ordenados por nombre.
     *
     * @return lista de productos activos
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    /**
     * Obtiene todos los productos incluyendo inactivos.
     *
     * @return lista completa de productos
     */
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su identificador unico.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     * @throws IllegalArgumentException si no existe
     */
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro el producto con ID: " + id));
    }

    /**
     * Busca productos cuyo nombre contenga el texto indicado.
     *
     * CP-004 - Flujo normal paso 3: el sistema muestra la informacion solicitada.
     * CP-004 - Flujo alternativo: si no encuentra resultado retorna lista vacia
     *          y la vista muestra el mensaje "No se encontraron productos".
     *
     * @param nombre texto a buscar en el nombre del producto
     * @return lista de productos que coinciden
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene los productos con stock igual o por debajo del stock minimo.
     *
     * CP-008 - Alertas y notificaciones:
     * El sistema controla las cantidades minimas y genera alerta cuando
     * los productos estan en el punto mas bajo.
     * Criterio de aceptacion: las alertas deben ser oportunas y precisas,
     * priorizando productos con el stock mas bajo.
     *
     * @return lista de productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     *
     * CP-001 - Flujo normal: ingresa nombre, codigo, cantidad, proveedor,
     * fecha de vencimiento -> se registra en la BD -> el stock queda actualizado.
     * CP-001 - Flujo alternativo: si el codigo ya existe lanza excepcion con
     * mensaje de alerta al usuario (criterio de aceptacion: stock actualizado).
     *
     * @param producto objeto con los datos del nuevo producto
     * @return el producto guardado con su ID asignado
     * @throws IllegalArgumentException si ya existe un producto con ese codigo
     */
    @Transactional
    public Producto guardar(Producto producto) {
        // CP-001: Verificar unicidad del codigo antes de registrar
        if (producto.getCodigoProducto() != null && !producto.getCodigoProducto().isEmpty()) {
            Optional<Producto> existente = productoRepository
                    .findByCodigoProducto(producto.getCodigoProducto());
            if (existente.isPresent() &&
                !existente.get().getIdProducto().equals(producto.getIdProducto())) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con el codigo: " + producto.getCodigoProducto());
            }
        }
        return productoRepository.save(producto);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id       identificador del producto a actualizar
     * @param producto objeto con los nuevos datos
     * @return el producto actualizado
     */
    @Transactional
    public Producto actualizar(Long id, Producto producto) {
        // Verificar que el producto existe antes de actualizar
        buscarPorId(id);
        producto.setIdProducto(id);
        return productoRepository.save(producto);
    }

    /**
     * Realiza un borrado logico del producto (marca como inactivo).
     * El registro permanece en BD pero no aparece en consultas normales.
     *
     * @param id identificador del producto a desactivar
     */
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false); // Borrado logico: preserva el historial
        productoRepository.save(producto);
    }

    /**
     * Actualiza unicamente la cantidad en stock de un producto.
     *
     * CP-002 - Flujo normal pasos 4-5: ingresa nueva cantidad y guarda cambios.
     * CP-002 - Flujo alternativo: si la cantidad es erronea el sistema envia
     * error y pide correccion (lanzando IllegalArgumentException).
     * Criterio de aceptacion: se evidencian los cambios con el usuario
     * que realiza la modificacion.
     *
     * @param id       identificador del producto
     * @param cantidad nueva cantidad en stock (debe ser mayor o igual a 0)
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    @Transactional
    public void actualizarStock(Long id, Integer cantidad) {
        // CP-002: Validar cantidad antes de actualizar (flujo alternativo)
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo. Corrija la cantidad ingresada.");
        }
        Producto producto = buscarPorId(id);
        producto.setStock(cantidad);
        productoRepository.save(producto);
    }
}
