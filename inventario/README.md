# Sistema de Gestión de Inventario
### Proyecto SENA – Frameworks para construcción de aplicaciones con JAVA

---

## Tecnologías utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.0 | Framework principal, servidor embebido |
| Spring MVC | (incluido) | Controladores y vistas web |
| Hibernate / JPA | (incluido) | Mapeo objeto-relacional (ORM) |
| Thymeleaf | (incluido) | Motor de plantillas HTML |
| MySQL | 8.0+ | Base de datos relacional |
| Maven | 3.8+ | Gestión de dependencias |
| Bootstrap | 5.3 | Estilos y componentes visuales |
| Lombok | (incluido) | Reducción de código repetitivo |

---

## Estructura del proyecto

```
gestion-inventario/
├── src/
│   └── main/
│       ├── java/com/inventario/
│       │   ├── InventarioApplication.java     ← Clase principal (main)
│       │   ├── model/                         ← Entidades JPA (tablas)
│       │   │   ├── Producto.java
│       │   │   ├── Categoria.java
│       │   │   └── Proveedor.java
│       │   ├── repository/                    ← Acceso a BD (Hibernate)
│       │   │   ├── ProductoRepository.java
│       │   │   ├── CategoriaRepository.java
│       │   │   └── ProveedorRepository.java
│       │   ├── service/                       ← Lógica de negocio
│       │   │   ├── ProductoService.java
│       │   │   ├── CategoriaService.java
│       │   │   └── ProveedorService.java
│       │   └── controller/                    ← Spring MVC (HTTP)
│       │       ├── HomeController.java
│       │       └── ProductoController.java
│       └── resources/
│           ├── application.properties          ← Configuración MySQL
│           └── templates/                      ← Vistas Thymeleaf
│               ├── index.html                  ← Dashboard principal
│               └── productos/
│                   ├── lista.html
│                   └── formulario.html
├── pom.xml                                     ← Dependencias Maven
└── README.md
```

---

## Configuración de la base de datos (MySQL Workbench)

### Paso 1 – Crear la base de datos

Abre MySQL Workbench y ejecuta:

```sql
-- Crear la base de datos
CREATE DATABASE inventario_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE inventario_db;
```

> Hibernate creará las tablas automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`

### Paso 2 – Configurar la contraseña

Edita el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA_AQUI
```

---

## Cómo ejecutar el proyecto

### Prerequisitos
- Java 17 instalado (`java -version`)
- Maven instalado (`mvn -version`)
- MySQL Workbench corriendo con la BD creada

### Ejecutar desde terminal

```bash
# Clonar el repositorio (después de subirlo a GitHub)
git clone https://github.com/TU_USUARIO/gestion-inventario.git
cd gestion-inventario

# Compilar y ejecutar
mvn spring-boot:run
```

### Ejecutar desde IntelliJ IDEA / Eclipse
1. Importar como proyecto Maven
2. Esperar a que descargue las dependencias
3. Ejecutar `InventarioApplication.java` → botón ▶ Run

### Acceder a la aplicación
Abre el navegador en: **http://localhost:8080**

---

## Versionamiento con Git y GitHub

### Configuración inicial (una sola vez)

```bash
# Configurar tu identidad en Git
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

### Inicializar el repositorio local

```bash
# Dentro de la carpeta del proyecto
cd gestion-inventario

# Inicializar Git
git init

# Agregar todos los archivos
git add .

# Primer commit
git commit -m "feat: proyecto inicial - sistema gestión inventario

- Estructura Spring Boot + Hibernate/JPA + Thymeleaf
- Entidades: Producto, Categoria, Proveedor
- CRUD completo de productos con validaciones
- Dashboard con alertas de stock bajo
- Conexión configurada para MySQL"
```

### Subir a GitHub

```bash
# 1. Crear repositorio en github.com (sin README, sin .gitignore)
# 2. Conectar tu proyecto local con GitHub:
git remote add origin https://github.com/TU_USUARIO/gestion-inventario.git

# 3. Subir el código
git branch -M main
git push -u origin main
```

### Flujo de trabajo recomendado

```bash
# Después de hacer cambios en el código:

# Ver qué archivos cambiaron
git status

# Agregar cambios al área de preparación
git add .

# Registrar el commit con mensaje descriptivo
git commit -m "feat: agregar módulo de categorías con CRUD completo"

# Subir cambios a GitHub
git push
```

### Convención para mensajes de commit

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Cambio en documentación |
| `refactor:` | Mejora de código sin cambiar funcionalidad |
| `style:` | Cambios de formato/estilo |

### Archivo .gitignore recomendado

Crea un archivo `.gitignore` en la raíz del proyecto:

```gitignore
# Compilados de Java
target/
*.class

# IntelliJ IDEA
.idea/
*.iml

# Eclipse
.classpath
.project
.settings/

# Spring Boot
*.jar
*.war

# Variables de entorno (no subir contraseñas)
application-local.properties
```

---

## Funcionalidades implementadas

- [x] Dashboard con estadísticas del inventario
- [x] CRUD completo de Productos
- [x] Gestión de Categorías
- [x] Gestión de Proveedores
- [x] Validaciones de formularios (Bean Validation)
- [x] Borrado lógico de productos (campo `activo`)
- [x] Alertas de stock bajo
- [x] Búsqueda de productos por nombre
- [x] Mensajes de éxito/error en operaciones

---

## Autor

**[Tu nombre completo]**
Aprendiz SENA – Análisis y Desarrollo de Software
Ficha: [número de ficha]
