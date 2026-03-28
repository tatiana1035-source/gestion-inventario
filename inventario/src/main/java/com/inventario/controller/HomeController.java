package com.inventario.controller;

import com.inventario.service.CategoriaService;
import com.inventario.service.ProductoService;
import com.inventario.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para el Dashboard principal del sistema de inventario.
 *
 * Historia de usuario que implementa este controlador:
 *
 * CP-008 - Como lider de inventario, quiero recibir alertas cuando un
 *          producto alcance el nivel minimo para gestionar su oportuno
 *          abastecimiento.
 *
 * Criterios de aceptacion CP-008:
 * - Las alertas deben ser oportunas y precisas.
 * - Priorizar productos con el stock mas bajo.
 * - El dashboard muestra un resumen del estado del inventario.
 *
 * Caso de uso relacionado: "Alertas y notificaciones"
 * Actor: Lider de inventario
 * Precondicion: El usuario debe haber iniciado sesion.
 * Flujo normal: el sistema controla las cantidades minimas, cuando los
 * productos estan en el punto mas bajo genera y muestra la alerta.
 *
 * @author Yuli Tatiana Moreno Vasquez
 * @version 1.0.0
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    /** Servicio de productos (CP-001, CP-002, CP-008). */
    private final ProductoService productoService;

    /** Servicio de categorias. */
    private final CategoriaService categoriaService;

    /** Servicio de proveedores (CP-006). */
    private final ProveedorService proveedorService;

    /**
     * Muestra el dashboard principal con estadisticas del inventario.
     *
     * CP-008 - Flujo normal: el sistema verifica si algun producto alcanzo
     * el nivel minimo y muestra la alerta en pantalla con la lista de
     * productos afectados para que el lider tome accion.
     *
     * GET /
     *
     * @param model modelo para pasar datos a la vista Thymeleaf
     * @return plantilla del dashboard principal (index.html)
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        // Estadisticas generales para las tarjetas del dashboard
        model.addAttribute("totalProductos",
                productoService.listarProductosActivos().size());
        model.addAttribute("totalCategorias",
                categoriaService.listarTodas().size());
        model.addAttribute("totalProveedores",
                proveedorService.listarTodos().size());

        // CP-008: Lista de productos con stock bajo para mostrar alertas
        model.addAttribute("stockBajo",
                productoService.obtenerProductosStockBajo());
        model.addAttribute("alertasStockBajo",
                productoService.obtenerProductosStockBajo().size());

        return "index";
    }
}
