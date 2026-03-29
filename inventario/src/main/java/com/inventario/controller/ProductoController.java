package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.service.CategoriaService;
import com.inventario.service.ProductoService;
import com.inventario.service.ProveedorService;
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
 *   <li><b>CP-008</b> – Como líder de inventario, quiero recibir alertas cuando
 *       un producto alcance el nivel mínimo de stock.</li>
 * </ul>
 *
 * <p><b>Nota técnica:</b> Se omite @Valid en guardar() y actualizar() porque
 * Spring ejecuta la validación ANTES de que el método pueda asignar la categoría
 * y el proveedor desde los RequestParam. Esto causaba que @NotNull en categoria
 * fallara falsamente. La validación se realiza manualmente dentro de cada método.</p>
 *
 * @author Yuli Tatiana Moreno Vásquez
 * @version 1.2.0
 */
@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    // ── LISTADO ──
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarProductosActivos());
        model.addAttribute("stockBajo", productoService.obtenerProductosStockBajo());
        return "productos/lista";
    }

    // ── BUSCAR (CP-004) ──
    @GetMapping("/buscar")
    public String buscar(@RequestParam("nombre") String nombre, Model model) {
        model.addAttribute("productos", productoService.buscarPorNombre(nombre));
        model.addAttribute("busqueda", nombre);
        model.addAttribute("stockBajo", productoService.obtenerProductosStockBajo());
        return "productos/lista";
    }

    // ── FORMULARIO NUEVO ──
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("titulo", "Nuevo Producto");
        return "productos/formulario";
    }

    // ── GUARDAR NUEVO (CP-001) ──
    // NOTA: Sin @Valid — la validación se hace manualmente después de asignar
    // categoría y proveedor para evitar falsos errores de @NotNull.
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("producto") Producto producto,
                          BindingResult result,
                          @RequestParam(value = "proveedorId", required = false) Long proveedorId,
                          @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Asignar categoría y proveedor PRIMERO antes de validar
        if (categoriaId != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(categoriaId);
            producto.setCategoria(categoria);
        }

        if (proveedorId != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(proveedorId);
            producto.setProveedor(proveedor);
        } else {
            producto.setProveedor(null);
        }

        // Validación manual de campos obligatorios
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            result.rejectValue("nombre", "error.nombre", "El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().doubleValue() <= 0) {
            result.rejectValue("precio", "error.precio", "El precio debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            result.rejectValue("stock", "error.stock", "El stock no puede ser negativo");
        }
        if (producto.getCategoria() == null) {
            result.rejectValue("categoria", "error.categoria", "La categoría es obligatoria");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Nuevo Producto");
            return "productos/formulario";
        }

        try {
            productoService.guardar(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto guardado exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Nuevo Producto");
            return "productos/formulario";
        }

        return "redirect:/productos";
    }

    // ── FORMULARIO EDITAR ──
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("titulo", "Editar Producto");
        return "productos/formulario";
    }

    // ── ACTUALIZAR (CP-001 / CP-002) ──
    // NOTA: Sin @Valid — misma razón que guardar().
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("producto") Producto producto,
                             BindingResult result,
                             @RequestParam(value = "proveedorId", required = false) Long proveedorId,
                             @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        // Asignar categoría y proveedor PRIMERO antes de validar
        if (categoriaId != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(categoriaId);
            producto.setCategoria(categoria);
        }

        if (proveedorId != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(proveedorId);
            producto.setProveedor(proveedor);
        } else {
            producto.setProveedor(null);
        }

        // Validación manual de campos obligatorios
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            result.rejectValue("nombre", "error.nombre", "El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().doubleValue() <= 0) {
            result.rejectValue("precio", "error.precio", "El precio debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            result.rejectValue("stock", "error.stock", "El stock no puede ser negativo");
        }
        if (producto.getCategoria() == null) {
            result.rejectValue("categoria", "error.categoria", "La categoría es obligatoria");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Editar Producto");
            return "productos/formulario";
        }

        try {
            productoService.actualizar(id, producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("titulo", "Editar Producto");
            return "productos/formulario";
        }

        return "redirect:/productos";
    }

    // ── ACTUALIZAR SOLO STOCK (CP-002) ──
    @PostMapping("/stock/{id}")
    public String actualizarStock(@PathVariable Long id,
                                  @RequestParam("cantidad") Integer cantidad,
                                  RedirectAttributes redirectAttributes) {
        try {
            productoService.actualizarStock(id, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExito", "Stock actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/productos";
    }

    // ── ELIMINAR (borrado lógico) ──
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/productos";
    }

    // ── DETALLE ──
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        return "productos/detalle";
    }
}