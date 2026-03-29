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
 * @author Yuli Tatiana Moreno Vasquez
 * @version 1.1.0
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos activos del inventario ordenados por nombre.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    /**
     * Obtiene todos los productos incluyendo inactivos.
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
     * CP-004 - Flujo normal paso 3.
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene los productos con stock igual o por debajo del stock minimo.
     * CP-008 - Alertas de stock bajo.
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     * CP-001 - Flujo normal y flujo alternativo (codigo duplicado).
     *
     * @param producto objeto con los datos del nuevo producto
     * @return el producto guardado con su ID asignado
     * @throws IllegalArgumentException si ya existe un producto con ese codigo
     */
    @Transactional
    public Producto guardar(Producto producto) {
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
     * FIX: Se preservan fechaRegistro y activo del registro original
     * para evitar que Hibernate los sobreescriba con null al hacer save().
     *
     * @param id       identificador del producto a actualizar
     * @param producto objeto con los nuevos datos
     * @return el producto actualizado
     */
    @Transactional
    public Producto actualizar(Long id, Producto producto) {
        // Cargar el registro original para recuperar campos no editables
        Producto existente = buscarPorId(id);

        producto.setIdProducto(id);

        // FIX Bug #2: Preservar fechaRegistro (el @PrePersist no se dispara en update)
        producto.setFechaRegistro(existente.getFechaRegistro());

        // FIX Bug #2: Preservar el estado activo para no reactivar productos eliminados
        producto.setActivo(existente.getActivo());

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
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    /**
     * Actualiza unicamente la cantidad en stock de un producto.
     *
     * CP-002 - Flujo normal pasos 4-5: ingresa nueva cantidad y guarda cambios.
     * CP-002 - Flujo alternativo: si la cantidad es negativa lanza excepcion.
     *
     * @param id       identificador del producto
     * @param cantidad nueva cantidad en stock (debe ser mayor o igual a 0)
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    @Transactional
    public void actualizarStock(Long id, Integer cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo. Corrija la cantidad ingresada.");
        }
        Producto producto = buscarPorId(id);
        producto.setStock(cantidad);
        productoRepository.save(producto);
    }
}