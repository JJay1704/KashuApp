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
│   │   │   ├── core/               # Componentes reutilizables y navegación global
│   │   │   │   ├── composables/    # Botones, logos y widgets genéricos
│   │   │   │   └── navBar/         # HomeTab y estructura de pestañas
│   │   │   ├── data/               # Repositorios y cliente backend (Supabase)
│   │   │   │   ├── KashuSupaBase.kt
│   │   │   │   ├── IAuthRepository.kt / SupabaseAuthRepository.kt
│   │   │   │   └── ITransactionRepository.kt / SupabaseTransactionRepository.kt
│   │   │   ├── ui/                 # Vistas y ViewModels organizados por pantalla/módulo
│   │   │   │   ├── home/           # Dashboard principal, saldo y resumen
│   │   │   │   ├── login/          # LoginScreen, LoginViewModel, LoginScreenState
│   │   │   │   ├── register/       # Registro de nuevos usuarios
│   │   │   │   ├── transaction/    # Historial y registro de transacciones
│   │   │   │   └── theme/          # Sistema de diseño (Color.kt, Theme.kt, Type.kt)
│   │   │   └── MainActivity.kt     # Punto de entrada de la actividad y NavHost
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
| **Backend & Base de Datos** | Supabase (Supabase BOM `3.1.4`, Auth, Postgrest Database con PostgreSQL + RLS, Ktor OkHttp) |
| **Serialización** | Kotlinx Serialization (`kotlinx.serialization`) |
| **Gestor de Construcción** | Gradle KTS con Version Catalogs (`libs.versions.toml`) |

---

## 4. Estándares de Arquitectura y Código

### 4.1. Arquitectura Recomendada (MVVM + Clean Architecture)
- **UI Layer (`ui/<modulo>/...`)**:
  - Pantallas Composable (`Screen.kt`) puramente declarativas.
  - **State Hoisting**: Los composables deben recibir el estado (State) y emitir eventos mediante lambdas (`onAction: () -> Unit`). Evitar instanciar o mutar estado global directamente dentro del composable.
  - Separar componentes reutilizables en funciones composables pequeñas y privadas si aplican solo a esa pantalla, o en componentes compartidos si son globales.
- **State Management & ViewModel**:
  - Utilizar `ViewModel` (`androidx.lifecycle.viewmodel.compose`) para manejar el estado de la UI (`StateFlow` / `asStateFlow()`).
  - Nunca colocar llamadas a APIs, lógica de negocio o cálculos financieros complejos directamente dentro de una función `@Composable`.
- **Data Layer**:
  - Repositorios (`Repository`) como fuente única de verdad entre la UI/ViewModel y los servicios de persistencia (Supabase / DataStore / Room).

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

### 8.1. Freno de Mano Proactivo: Evaluación de Trade-offs antes de Construir
- **Advertir antes de ejecutar:** Cuando el usuario proponga una idea o diseño de datos, el agente tiene la **obligación estricta de evaluar los trade-offs de antemano**:
  1. ¿Genera redundancia masiva o desperdicio de almacenamiento en la base de datos (ej. duplicar catálogos que deberían ser globales)?
  2. ¿Constituye una pérdida de tiempo o sobreingeniería que complique innecesariamente el mantenimiento o la sustentación académica?
  3. ¿Se desvía de cómo se resuelve este problema en arquitecturas y aplicaciones del mundo real?
- **Protocolo de objeción inmediata:** Si se detecta cualquiera de estos riesgos, el agente **NO debe proceder a escribir código o esquemas obedeciendo ciegamente la instrucción**. Debe detenerse, explicar con argumentos directos por qué ese camino es problemático y presentar la alternativa estándar de la industria antes de continuar.

---

## 9. Protocolo de Verificación para Entrega T1 (Checklist Mandatorio)

Cuando el usuario pregunte *"¿el proyecto está listo para el T1?"*, *"¿estamos listos para la entrega 1?"*, *"verifica el T1"* o cualquier variante similar, el agente **DEBE activar inmediatamente una auditoría estricta del código fuente** contra el alcance oficial del T1.

### 9.1. Alcance y Criterios de Aceptación del T1
El agente debe evaluar punto por punto los siguientes requerimientos:

1. **HU-01 / RF-01: Autenticación y Acceso Seguro**
   - **HU-01:** *Como usuario quiero registrarme e iniciar sesión con mi correo electrónico y una contraseña, para poder acceder a mi perfil de forma segura y privada desde cualquier dispositivo.*
   - **RF-01:** *El sistema debe permitir al usuario registrarse e iniciar sesión ingresando un correo electrónico válido y una contraseña segura, validando que el correo no esté registrado previamente.*
   - **Acción:** El usuario ingresa un correo electrónico válido y una contraseña segura en el formulario de acceso o creación de cuenta.
   - **Resultado esperado:** El sistema valida credenciales únicas y no repetidas, otorgando acceso mediante una sesión activa o rechazando con un mensaje de error genérico/adecuado.

2. **HU-03 / RF-03: Registro Manual de Transacciones e Impacto en Balance**
   - **HU-03:** *Como usuario quiero registrar manualmente mis ingresos, gastos especificando monto, fecha, hora, categoría y cuenta para llevar control detallado de mi dinero.*
   - **RF-03:** *El sistema debe proveer una interfaz para el registro manual seleccionando el tipo de movimiento (ingreso o gasto).*
   - **Acción:** El usuario ingresa una transacción seleccionando tipo, monto mayor a cero (`> 0`), fecha, hora, categoría y cuenta.
   - **Resultado esperado:** El sistema valida los datos obligatorios, almacena el movimiento e impacta directamente en el balance de la cuenta seleccionada.

3. **HU-04 / RF-04: Gestión de Categorías de Gasto**
   - **HU-04:** *Como usuario quiero clasificar y renombrar mis categorías de gasto, para organizar la información financiera según mi criterio.*
   - **RF-04:** *El sistema debe permitir al usuario crear, editar el nombre y eliminar o desactivar categorías personalizadas de gasto.*
   - **Acción:** El usuario crea, renombra o desactiva categorías y les asigna identificadores visuales (ícono y color).
   - **Resultado esperado:** El sistema actualiza el catálogo personal y lo refleja en todas las vistas operativas (ej. selectores en el registro de transacciones).

4. **Arquitectura de Software (MVVM + Clean Architecture) y Principios SOLID**
   - **Arquitectura y Jetpack Compose:**
     - **State Hoisting:** Composables puramente declarativos que reciben estado inmutable y emiten eventos por lambdas (`onAction: () -> Unit`). Cero lógica de negocio o llamadas a repositorios dentro de `@Composable`.
     - **UI Layer / ViewModel:** Manejo de estado centralizado con `StateFlow` / `asStateFlow()`, corrutinas en `viewModelScope` y modelado explícito de estados (`Loading`, `Success`, `Error`).
     - **Data Layer:** Repositorios como fuente única de verdad desacoplados del cliente de base de datos/red.
   - **Principios SOLID:**
     - **S (Single Responsibility):** Clases, ViewModels y Composables con una única razón para cambiar.
     - **O (Open/Closed):** Jerarquías de estados y eventos modeladas con `sealed class` / `sealed interface`.
     - **L (Liskov Substitution):** Implementaciones de repositorios sustituibles sin romper el contrato esperado.
     - **I (Interface Segregation):** Interfaces segregadas y concisas (ej. `IAuthRepository`, `ITransactionRepository`, `ICategoryRepository`), sin métodos innecesarios.
     - **D (Dependency Inversion):** Los ViewModels dependen de contratos/interfaces (`IRepository`), no de implementaciones concretas o SDKs acoplados.

### 9.2. Formato del Reporte de Verificación T1
La respuesta del agente debe estructurarse obligatoriamente con el siguiente formato:

- **Tabla de Estado Funcional T1 (HU / RF):**
  - Columnas: *Requisito / HU*, *Criterio Evaluado*, *Estado* (`[OK] Cumplido`, `[PARCIAL] En progreso`, `[PENDIENTE] No implementado`), *Evidencia / Ubicación en Código* (archivo y componente/línea).
- **Tabla de Auditoría Técnica (Arquitectura MVVM & SOLID):**
  - Columnas: *Principio / Pilar*, *Criterio Evaluado*, *Estado* (`[OK]`, `[VIOLACIÓN]`, `[PARCIAL]`), *Evidencia / Ubicación en Código*.
- **Análisis de Gaps y Pendientes Críticos:**
  - Lista detallada de lo que falta para cumplir tanto funcionalmente como técnicamente (validaciones, endpoints, acoplamientos, violaciones de capas).
- **Veredicto Final Riguroso:**
  - Dictamen claro: **"LISTO PARA T1"** o **"NO ESTÁ LISTO PARA T1"**, fundamentando exactamente qué bloquea la entrega sin complacencias.

