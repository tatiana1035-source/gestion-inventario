package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.service.CategoriaService;
import com.inventario.service.ProductoService;
import com.inventario.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Spring MVC para la gestión de {@link Producto}.
 *
 * <p><b>Historias de usuario implementadas:</b></p>
 * <ul>
 *   <li><b>CP-001</b> – Como líder de inventario, quiero registrar nuevos productos
 *       para mantener el stock actualizado.</li>
 *   <li><b>CP-002</b> – Como auxiliar de inventario, quiero actualizar la cantidad
 *       de productos disponibles para reflejar cambios en el stock.</li>
 *   <li><b>CP-004</b> – Como auxiliar de inventario, quiero buscar productos
 *       rápidamente para consultar su información por categorías.</li>
 * </ul>
 *
 * <p>Criterios de aceptación CP-001: se ingresan todos los datos y el stock
 * queda actualizado. Implementado en {@link #guardar}.</p>
 *
 * <p>Criterios de aceptación CP-002: se confirma que el producto existe y
 * se evidencian los cambios. Implementado en {@link #actualizar}.</p>
 *
 * <p>Sigue el patrón MVC: recibe la petición HTTP, invoca el servicio
 * de negocio y retorna el nombre de la vista Thymeleaf a renderizar.</p>
 *
 * <p>URL base: {@code /productos}</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    /** Servicio de lógica de negocio para productos. */
    private final ProductoService productoService;

    /** Servicio para obtener la lista de categorías en los formularios. */
    private final CategoriaService categoriaService;

    /** Servicio para obtener la lista de proveedores en los formularios. */
    private final ProveedorService proveedorService;

    // ─────────────────────────────────────────────────────────────────────
    // LISTADO
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Muestra el listado de todos los productos activos.
     * También muestra alertas de productos con stock bajo.
     *
     * <p>GET /productos</p>
     *
     * @param model objeto que lleva datos desde el controlador a la vista
     * @return nombre de la plantilla Thymeleaf: {@code productos/lista}
     */
    @GetMapping
    public String listar(Model model) {
        // Cargar productos activos
        model.addAttribute("productos", productoService.listarProductosActivos());

        // Alerta de stock bajo para mostrar en la vista
        model.addAttribute("stockBajo", productoService.obtenerProductosStockBajo());

        return "productos/lista";
    }

    /**
     * Busca productos por nombre y muestra los resultados.
     *
     * <p>GET /productos/buscar?nombre=texto</p>
     *
     * @param nombre texto a buscar
     * @param model  modelo para la vista
     * @return nombre de la plantilla de listado
     */
    @GetMapping("/buscar")
    public String buscar(@RequestParam("nombre") String nombre, Model model) {
        model.addAttribute("productos", productoService.buscarPorNombre(nombre));
        model.addAttribute("busqueda", nombre);
        return "productos/lista";
    }

    // ─────────────────────────────────────────────────────────────────────
    // CREACIÓN
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Muestra el formulario para crear un nuevo producto.
     *
     * <p>GET /productos/nuevo</p>
     *
     * @param model modelo para la vista
     * @return nombre de la plantilla del formulario
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        // Se envía un objeto vacío para que Thymeleaf lo enlace al formulario
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("titulo", "Nuevo Producto");
        return "productos/formulario";
    }

    /**
     * Procesa el formulario de creación de un nuevo producto.
     * Si hay errores de validación, regresa al formulario con los mensajes de error.
     *
     * <p>POST /productos/guardar</p>
     *
     * @param producto           objeto con los datos del formulario (validado automáticamente)
     * @param result             resultado de la validación (errores de campos)
     * @param redirectAttributes atributos para pasar mensajes al redirigir
     * @param model              modelo de la vista (para errores)
     * @return redirección al listado si exitoso, o el formulario si hay errores
     */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Si hay errores de validación, regresar al formulario con mensajes de error
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Nuevo Producto");
            return "productos/formulario";
        }

        try {
            productoService.guardar(producto);
            // Mensaje de éxito que se mostrará en el listado
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Producto guardado exitosamente.");
        } catch (IllegalArgumentException e) {
            // Error de negocio (ej: código duplicado)
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Nuevo Producto");
            return "productos/formulario";
        }

        return "redirect:/productos";
    }

    // ─────────────────────────────────────────────────────────────────────
    // EDICIÓN
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Muestra el formulario para editar un producto existente.
     *
     * <p>GET /productos/editar/{id}</p>
     *
     * @param id    identificador del producto a editar
     * @param model modelo para la vista
     * @return formulario con los datos actuales del producto
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("titulo", "Editar Producto");
        return "productos/formulario";
    }

    /**
     * Procesa la actualización de un producto existente.
     *
     * <p>POST /productos/actualizar/{id}</p>
     *
     * @param id                 identificador del producto
     * @param producto           datos actualizados del formulario
     * @param result             resultado de validaciones
     * @param redirectAttributes mensajes flash para la redirección
     * @param model              modelo de la vista
     * @return redirección al listado si exitoso, o formulario si hay errores
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("producto") Producto producto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Editar Producto");
            return "productos/formulario";
        }

        productoService.actualizar(id, producto);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Producto actualizado exitosamente.");
        return "redirect:/productos";
    }

    // ─────────────────────────────────────────────────────────────────────
    // ELIMINACIÓN
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Realiza el borrado lógico de un producto (lo marca como inactivo).
     *
     * <p>GET /productos/eliminar/{id}</p>
     *
     * @param id                 identificador del producto a eliminar
     * @param redirectAttributes mensaje flash para confirmar la operación
     * @return redirección al listado de productos
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        productoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Producto eliminado exitosamente.");
        return "redirect:/productos";
    }

    /**
     * Muestra el detalle completo de un producto.
     *
     * <p>GET /productos/detalle/{id}</p>
     *
     * @param id    identificador del producto
     * @param model modelo para la vista
     * @return vista de detalle del producto
     */
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        return "productos/detalle";
    }
}
