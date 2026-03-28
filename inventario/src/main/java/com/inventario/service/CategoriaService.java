package com.inventario.service;

import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de {@link Categoria}.
 *
 * <p>Maneja la lógica de creación, consulta, actualización y eliminación
 * de categorías de productos.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CategoriaService {

    /** Repositorio JPA para acceso a la tabla categorias. */
    private final CategoriaRepository categoriaRepository;

    /**
     * Obtiene todas las categorías ordenadas alfabéticamente.
     *
     * @return lista de categorías
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return la categoría encontrada
     * @throws IllegalArgumentException si no existe
     */
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la categoría con ID: " + id));
    }

    /**
     * Guarda una nueva categoría validando que el nombre no esté duplicado.
     *
     * @param categoria datos de la nueva categoría
     * @return la categoría guardada
     * @throws IllegalArgumentException si ya existe una categoría con ese nombre
     */
    @Transactional
    public Categoria guardar(Categoria categoria) {
        // Validar que no exista otra categoría con el mismo nombre
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param id        identificador de la categoría
     * @param categoria nuevos datos
     * @return la categoría actualizada
     */
    @Transactional
    public Categoria actualizar(Long id, Categoria categoria) {
        buscarPorId(id);  // Verificar que existe
        categoria.setIdCategoria(id);
        return categoriaRepository.save(categoria);
    }

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id identificador de la categoría a eliminar
     * @throws IllegalArgumentException si no existe
     */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);  // Verificar que existe antes de eliminar
        categoriaRepository.deleteById(id);
    }
}
