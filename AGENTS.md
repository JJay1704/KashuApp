# Reglas y Guía de Agentes — KashuApp

Este archivo (`AGENTS.md`) define el contexto, estándares de arquitectura, lineamientos de código y reglas de interacción que cualquier agente de inteligencia artificial debe seguir al trabajar en el repositorio **KashuApp**.

---

## 1. Visión General del Proyecto

- **Nombre del proyecto:** KashuApp
- **Tipo de proyecto:** Aplicación móvil nativa para Android de gestión financiera personal (control de ingresos, gastos, presupuestos mensuales, metas de ahorro, patrimonio neto y reportes analíticos).
- **Entorno y contexto:** Proyecto universitario de la materia *Desarrollo de Aplicaciones Móviles*.
- **Enfoque clave:** El código debe ser modular, escalable, idiomático en Kotlin y Jetpack Compose, pero manteniendo una alta **claridad pedagógica**: el estudiante debe ser capaz de entender, navegar y sustentar oralmente cada decisión, componente y línea de código.

---

## 2. Estructura del Repositorio

El espacio de trabajo está organizado en las siguientes carpetas principales:

```text
KashuApp/
├── .agents/                        # Configuración de agentes y habilidades personalizadas
│   └── skills/
│       └── preguntasSustentacion/  # Skill /sustentacion para simular evaluaciones orales
├── App/                            # Proyecto Android Studio (Gradle)
│   ├── app/
│   │   ├── src/main/java/com/kashuapp/
│   │   │   ├── feature/            # Módulos organizados por características de negocio
│   │   │   │   ├── auth/           # Login, registro, recuperación de contraseña
│   │   │   │   │   └── login/      # LoginScreen.kt, ViewModels asociados
│   │   │   │   ├── home/           # Dashboard principal, saldo total, patrimonio neto
│   │   │   │   ├── transaction/    # Registro, edición y filtrado de gastos/ingresos
│   │   │   │   ├── budget/         # Presupuestos por categoría y alertas
│   │   │   │   └── savings/        # Metas de ahorro y avance porcentual
│   │   │   ├── ui/theme/           # Sistema de diseño (Color.kt, Theme.kt, Type.kt)
│   │   │   └── MainActivity.kt     # Punto de entrada de la actividad principal
│   │   └── build.gradle.kts        # Configuración y dependencias del módulo app
│   ├── gradle/libs.versions.toml   # Catálogo centralizado de versiones (Version Catalog)
│   ├── build.gradle.kts            # Configuración raíz de Gradle
│   └── gradlew / gradlew.bat       # Wrapper de Gradle
├── Documentacion/                  # Especificaciones funcionales y diseño
│   ├── RequisitosFuncionales.md    # Historias de usuario (HU-01 a HU-12) y requisitos (RF-01 a RF-12.1)
│   ├── DiagramaDeFlujo.pdf         # Diagramas de flujo de navegación y procesos
│   └── Mockups/                    # Diseños visuales de referencia
└── AGENTS.md                       # Este archivo de directrices para agentes
```

---

## 3. Stack Tecnológico y Configuración

| Componente | Detalle / Versión |
| :--- | :--- |
| **Lenguaje** | Kotlin 2.2.x (usar sintaxis idiomática y corrutinas) |
| **UI Toolkit** | Jetpack Compose + Material 3 (`androidx.compose.material3`) |
| **Compose BOM** | `2026.02.01` (definida en `libs.versions.toml`) |
| **SDK Android** | `minSdk = 24` (Android 7.0 Nougat), `targetSdk = 37`, `compileSdk = 37` |
| **Backend & Servicios** | Firebase (Firebase BOM `34.19.0`, Analytics, Auth, Google Sign-In) |
| **Gestor de Construcción** | Gradle KTS con Version Catalogs (`libs.versions.toml`) |

---

## 4. Estándares de Arquitectura y Código

### 4.1. Arquitectura Recomendada (MVVM + Clean Architecture)
- **UI Layer (`feature/<modulo>/...`)**:
  - Pantallas Composable (`Screen.kt`) puramente declarativas.
  - **State Hoisting**: Los composables deben recibir el estado (State) y emitir eventos mediante lambdas (`onAction: () -> Unit`). Evitar instanciar o mutar estado global directamente dentro del composable.
  - Separar componentes reutilizables en funciones composables pequeñas y privadas si aplican solo a esa pantalla, o en componentes compartidos si son globales.
- **State Management & ViewModel**:
  - Utilizar `ViewModel` (`androidx.lifecycle.viewmodel.compose`) para manejar el estado de la UI (`StateFlow` / `asStateFlow()`).
  - Nunca colocar llamadas a APIs, lógica de negocio o cálculos financieros complejos directamente dentro de una función `@Composable`.
- **Data Layer**:
  - Repositorios (`Repository`) como fuente única de verdad entre la UI/ViewModel y los servicios de persistencia (Firebase / Room / DataStore).

### 4.2. Sistema de Diseño y Temas (Dark & Light Mode)
- Usar siempre la paleta definida en `com.kashuapp.ui.theme`:
  - **Color primario:** `KashuGreenPrimary` (`#34D399`), `KashuGreenDark` (`#10B981`)
  - **Modo Claro:** `KashuLightBackground` (`#F5F6F7`), `KashuLightSurface` (`#FFFFFF`), `KashuLightTitle` (`#121212`), `KashuLightSubtitle` (`#64748B`)
  - **Modo Oscuro:** `KashuDarkBackground` (`#121212`), `KashuDarkSurface` (`#1E2024`), `KashuDarkTitle` (`#FFFFFF`), `KashuDarkSubtitle` (`#9CA3AF`)
- Manejar soporte nativo dinámico evaluando `isSystemInDarkTheme()` o a través de los esquemas de color de MaterialTheme.

### 4.3. Calidad del Código y Limpieza
- **No dejar código comentado innecesario:** Si se reemplaza una implementación, eliminar el código viejo en lugar de dejar bloques masivos comentados.
- **Nombres descriptivos:** Variables, funciones y composables deben tener nombres claros y en inglés o español coherente con la base existente.
- **Validaciones:**
  - Montos numéricos siempre validados mayores a cero.
  - Formularios con validación en tiempo real o al presionar submit.
  - Manejo de excepciones y estados de carga (`Loading`, `Success`, `Error`).

---

## 5. Reglas de Ejecución de Comandos

Al interactuar con la terminal o ejecutar builds:
1. **Directorio de trabajo:** El proyecto Gradle reside en la carpeta `App/`. Cualquier comando Gradle (`./gradlew`, `gradlew.bat`) debe ejecutarse con `Cwd` apuntando a `d:\Universidad\DesarolloApp\KashuApp\App` (o subcarpeta respectiva).
2. **Comandos comunes:**
   - Compilar app: `.\gradlew assembleDebug`
   - Ejecutar pruebas unitarias: `.\gradlew test`
   - Comprobar lint: `.\gradlew lintDebug`
3. **No ejecutar `cd`**: Usar siempre el argumento `Cwd` en las herramientas correspondientes.

---

## 6. Trazabilidad con Requisitos Funcionales (`Documentacion/RequisitosFuncionales.md`)

Cualquier funcionalidad implementada debe responder a las historias de usuario y requisitos documentados:

- **Autenticación (HU-01, HU-02 / RF-01, RF-02):** Registro e inicio de sesión seguro (Email + Contraseña con políticas mínimas, Google Sign-In, Biometría), recuperación de contraseña por correo.
- **Transacciones (HU-03, HU-05, HU-11, HU-12 / RF-03, RF-05, RF-11, RF-12):** Registro manual de ingresos y gastos (monto, fecha, hora, categoría, cuenta), edición, historial reciente y búsqueda con filtros dinámicos.
- **Categorías (HU-04 / RF-04):** Clasificación de gastos, creación, edición, icono y color personalizable.
- **Presupuestos y Alertas (HU-06, HU-10 / RF-06, RF-10):** Asignación de tope monetario mensual por categoría, cálculo de porcentaje consumido y alertas al alcanzar 80% o 100%.
- **Metas de Ahorro (HU-07 / RF-07):** Registro de objetivo, monto meta, fecha límite y cálculo visual de avance y saldo faltante.
- **Dashboard y Estadísticas (HU-08, HU-09 / RF-08, RF-09):** Saldo total consolidado, patrimonio neto (activos - pasivos) y gráficos estadísticos (gastos e ingresos por categoría).

---

## 7. Instrucciones Específicas para Sustentaciones Académicas

1. **Habilidad `/sustentacion` disponible:**
   - En `.agents/skills/preguntasSustentacion/SKILL.md` está definida la dinámica de sustentación oral.
   - Si el usuario solicita practicar o preparar una sustentación, activar esta dinámica (preguntas progresivas: ubicación en el código -> comportamientos observables -> líneas puntuales).
2. **Priorizar la comprensión del estudiante:**
   - Evitar generar código con "magia negra" o patrones excesivamente intrincados que el estudiante no pueda explicar.
   - Cuando se introduzca un patrón nuevo (ej. `rememberCoroutineScope`, `LaunchedEffect`, `derivedStateOf`, Flow o State Hoisting), asegurarse de que quede claro su propósito y por qué es necesario en esa línea específica.

---

## 8. Criterio Técnico Estricto y Retroalimentación Crítica (Regla Mandatoria)

- **Honestidad y rigor técnico absoluto:** El agente NUNCA debe dar la razón al usuario por complacencia si una idea, afirmación o propuesta de código no es técnicamente correcta o constituye una mala práctica.
- **Contradecir fundamentadamente cuando sea necesario:** Si el usuario propone un enfoque subóptimo, un antipatrón (ej. sufijos vacíos como `Impl`, mezclar capas, violaciones a SOLID, etc.) o una interpretación errónea, el agente tiene la obligación de señalarlo directamente, llevar la contraria con argumentos de ingeniería de software sólidos y proponer la alternativa correcta.
- **Cero condescendencia o adulación innecesaria:** Mantener explicaciones pedagógicas, directas, objetivas y sin rodeos.

