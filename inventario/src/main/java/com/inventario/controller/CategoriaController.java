package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Spring MVC para la gestión de {@link Categoria}.
 *
 * <p>Maneja las peticiones HTTP para listar, crear, editar y
 * eliminar categorías de productos.</p>
 *
 * <p>URL base: {@code /categorias}</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    /** Servicio de lógica de negocio para categorías. */
    private final CategoriaService categoriaService;

    /**
     * Lista todas las categorías registradas.
     *
     * <p>GET /categorias</p>
     *
     * @param model modelo para la vista
     * @return plantilla de listado de categorías
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categorias/lista";
    }

    /**
     * Muestra el formulario para crear una nueva categoría.
     *
     * <p>GET /categorias/nueva</p>
     *
     * @param model modelo para la vista
     * @return plantilla del formulario
     */
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nueva Categoría");
        return "categorias/formulario";
    }

    /**
     * Procesa la creación de una nueva categoría.
     *
     * <p>POST /categorias/guardar</p>
     *
     * @param categoria          datos del formulario
     * @param result             resultado de validación
     * @param redirectAttributes mensajes flash
     * @param model              modelo de la vista
     * @return redirección al listado o formulario con errores
     */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nueva Categoría");
            return "categorias/formulario";
        }
        try {
            categoriaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Categoría creada exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("titulo", "Nueva Categoría");
            return "categorias/formulario";
        }
        return "redirect:/categorias";
    }

    /**
     * Muestra el formulario para editar una categoría existente.
     *
     * <p>GET /categorias/editar/{id}</p>
     *
     * @param id    identificador de la categoría
     * @param model modelo para la vista
     * @return formulario con datos actuales de la categoría
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        model.addAttribute("titulo", "Editar Categoría");
        return "categorias/formulario";
    }

    /**
     * Procesa la actualización de una categoría.
     *
     * <p>POST /categorias/actualizar/{id}</p>
     *
     * @param id                 identificador de la categoría
     * @param categoria          nuevos datos del formulario
     * @param result             resultado de validaciones
     * @param redirectAttributes mensajes flash
     * @param model              modelo de la vista
     * @return redirección al listado o formulario con errores
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("categoria") Categoria categoria,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Categoría");
            return "categorias/formulario";
        }
        categoriaService.actualizar(id, categoria);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Categoría actualizada exitosamente.");
        return "redirect:/categorias";
    }

    /**
     * Elimina una categoría por su identificador.
     *
     * <p>GET /categorias/eliminar/{id}</p>
     *
     * @param id                 identificador de la categoría
     * @param redirectAttributes mensaje de confirmación
     * @return redirección al listado
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        categoriaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Categoría eliminada exitosamente.");
        return "redirect:/categorias";
    }
}
