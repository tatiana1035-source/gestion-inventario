package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Spring MVC para la gestión de {@link Proveedor}.
 *
 * <p>Maneja las peticiones HTTP para listar, crear, editar y
 * eliminar proveedores del sistema de inventario.</p>
 *
 * <p>URL base: {@code /proveedores}</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.0.0
 */
@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    /** Servicio de lógica de negocio para proveedores. */
    private final ProveedorService proveedorService;

    /**
     * Lista todos los proveedores registrados.
     *
     * <p>GET /proveedores</p>
     *
     * @param model modelo para la vista
     * @return plantilla de listado de proveedores
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "proveedores/lista";
    }

    /**
     * Muestra el formulario para registrar un nuevo proveedor.
     *
     * <p>GET /proveedores/nuevo</p>
     *
     * @param model modelo para la vista
     * @return plantilla del formulario
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("titulo", "Nuevo Proveedor");
        return "proveedores/formulario";
    }

    /**
     * Procesa el registro de un nuevo proveedor.
     *
     * <p>POST /proveedores/guardar</p>
     *
     * @param proveedor          datos del formulario
     * @param result             resultado de validación
     * @param redirectAttributes mensajes flash
     * @param model              modelo de la vista
     * @return redirección al listado o formulario con errores
     */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nuevo Proveedor");
            return "proveedores/formulario";
        }
        proveedorService.guardar(proveedor);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Proveedor registrado exitosamente.");
        return "redirect:/proveedores";
    }

    /**
     * Muestra el formulario para editar un proveedor existente.
     *
     * <p>GET /proveedores/editar/{id}</p>
     *
     * @param id    identificador del proveedor
     * @param model modelo para la vista
     * @return formulario con datos actuales del proveedor
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarPorId(id));
        model.addAttribute("titulo", "Editar Proveedor");
        return "proveedores/formulario";
    }

    /**
     * Procesa la actualización de un proveedor existente.
     *
     * <p>POST /proveedores/actualizar/{id}</p>
     *
     * @param id                 identificador del proveedor
     * @param proveedor          nuevos datos del formulario
     * @param result             resultado de validaciones
     * @param redirectAttributes mensajes flash
     * @param model              modelo de la vista
     * @return redirección al listado o formulario con errores
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("proveedor") Proveedor proveedor,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Proveedor");
            return "proveedores/formulario";
        }
        proveedorService.actualizar(id, proveedor);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Proveedor actualizado exitosamente.");
        return "redirect:/proveedores";
    }

    /**
     * Elimina un proveedor por su identificador.
     *
     * <p>GET /proveedores/eliminar/{id}</p>
     *
     * @param id                 identificador del proveedor
     * @param redirectAttributes mensaje de confirmación
     * @return redirección al listado
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        proveedorService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Proveedor eliminado exitosamente.");
        return "redirect:/proveedores";
    }
}
