# Módulo de Metas de Ahorro Interactivas — KashuApp

> **Carpeta Aislada e Independiente:** Este módulo fue construido de forma totalmente desacoplada sin modificar ni escribir una sola línea de código dentro del proyecto ya desarrollado (`App/`).

---

## 1. Visión y Requisitos del Módulo

Este módulo cubre de forma integral la historia de usuario **HU-07** y los requisitos funcionales **RF-07** y **RF-07.1** de la documentación oficial de KashuApp:

- **HU-07:** *Como usuario quiero crear una meta de ahorro para un objetivo específico con fecha límite para visualizar cuánto me falta por alcanzar.*
- **RF-07:** *El sistema debe permitir registrar una meta especificando nombre del objetivo, monto meta y fecha límite de cumplimiento.*
- **RF-07.1:** *El sistema debe calcular y mostrar de manera gráfica el porcentaje de avance, el monto acumulado y la diferencia monetaria faltante para llegar a la meta.*
- **Diseño visual:** Inspirado fielmente en el mockup oficial (`Documentacion/Mockups/Goals/Opera Captura de pantalla_2026-08-29_201202_stitch.withgoogle.com.png`), implementando la sección **"Timeline & Motivation"** con carrusel horizontal (`LazyRow`), tarjetas de estado (*In Progress / Completed*), barra de progreso animada y tarjeta analítica de avance global.

---

## 2. Estructura de Archivos en la Carpeta Aislada

```text
ModuloMetas/
├── README.md                      # Esta guía técnica, arquitectónica y de sustentación
├── schema_supabase.sql            # Script SQL DDL con RLS y triggers para la tabla 'goal' en Supabase
├── data/
│   ├── Goal.kt                    # Data class con getters calculados (progressFraction, remainingAmount)
│   ├── IGoalRepository.kt         # Contrato desacoplado (Clean Architecture / SOLID)
│   └── GoalRepository.kt          # Implementación para Supabase Postgrest con filtros en servidor
└── ui/
    ├── GoalsState.kt              # Modelos inmutables de estado de UI y formulario
    ├── GoalsVM.kt                 # ViewModel con StateFlow, viewModelScope y validaciones en tiempo real
    ├── GoalsScreen.kt             # Composable principal con PullToRefresh, LazyRow y LazyColumn
    └── composables/
        ├── GoalCard.kt            # Tarjeta visual interactiva idéntica al mockup oficial
        ├── GoalFormDialog.kt      # Diálogo modal para registrar y editar metas
        └── AddSavingsDialog.kt    # Diálogo interactivo para abonar dinero con cálculo en vivo
```

---

## 3. Arquitectura y Principios SOLID Implementados

### 3.1. Patrón MVVM + Clean Architecture
1. **Domain / Data Layer (`data/`):**
   - [`Goal.kt`](data/Goal.kt): Modelo inmutable con validación de datos y cálculos derivados puros (cero lógica de UI).
   - [`IGoalRepository.kt`](data/IGoalRepository.kt): Contrato abstracto que encapsula las operaciones de red/persistencia.
   - [`GoalRepository.kt`](data/GoalRepository.kt): Consume Supabase Postgrest aplicando filtros directamente en base de datos (`eq("user_id", userId)`), optimizando latencia y consumo de datos móviles.
2. **UI / ViewModel Layer (`ui/`):**
   - [`GoalsVM.kt`](ui/GoalsVM.kt): Maneja el estado mediante `MutableStateFlow` privado expuesto como `StateFlow` inmutable (`asStateFlow()`). No utiliza `mutableStateOf` dentro del ViewModel, lo que permite realizar pruebas unitarias en JVM pura sin dependencias de Compose.
   - [`GoalsScreen.kt`](ui/GoalsScreen.kt): Composable 100% declarativo que consume el estado mediante `collectAsState()` y emite eventos hacia el ViewModel.

### 3.2. Cumplimiento de Principios SOLID
- **S (Single Responsibility):** Cada componente tiene una única responsabilidad: `GoalCard` solo renderiza la tarjeta; `GoalFormDialog` valida y captura datos; `GoalsVM` orquesta el flujo de negocio; `GoalRepository` realiza las llamadas a la red.
- **O (Open/Closed):** Los estados de UI (`GoalsState`, `GoalFormState`) son ampliables sin modificar la firma de los composables.
- **L (Liskov Substitution):** Cualquier clase que implemente `IGoalRepository` (ej. un `MockGoalRepository` para tests unitarios locales) puede sustituir a `GoalRepository` sin alterar el ViewModel.
- **I (Interface Segregation):** La interfaz `IGoalRepository` contiene únicamente métodos específicos del ciclo de vida de una meta y su abono.
- **D (Dependency Inversion):** `GoalsVM` depende de la abstracción `IGoalRepository`, no de una implementación concreta acoplada.

---

## 4. Guía de Integración Futura (Cuando decidas conectarlo al proyecto)

Para conectar este módulo al proyecto principal sin fricción cuando estés listo, solo requieres 3 pasos:

### Paso 1: Ejecutar el script SQL en Supabase
Copia el contenido de [`schema_supabase.sql`](schema_supabase.sql) y ejecútalo en el **SQL Editor** de tu consola de Supabase. Creará la tabla `goal`, sus políticas de seguridad RLS para usuarios autenticados y el trigger que marca automáticamente la meta como completada cuando se alcanza el monto objetivo.

### Paso 2: Mover los archivos a la carpeta del paquete Android
Copia las carpetas `data/` y `ui/` de `ModuloMetas/` hacia:
`App/app/src/main/java/com/kashuapp/goals/` (o la ruta de paquetes de tu preferencia).

### Paso 3: Conectar la vista en la navegación
En el archivo [`App/app/src/main/java/com/kashuapp/ui/home/HomeScreen.kt`](../App/app/src/main/java/com/kashuapp/ui/home/HomeScreen.kt#L104-L115), reemplaza el bloque placeholder del tab de metas:

```kotlin
// ANTES (Líneas 104-115 de HomeScreen.kt):
is HomeTab.Goals -> {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = "Metas de Ahorro", color = KashuTheme.colors.subtitle)
    }
}

// DESPUÉS (Conectando el módulo):
is HomeTab.Goals -> {
    com.kashuapp.goals.ui.GoalsScreen()
}
```

---

## 5. Guía de Sustentación Oral (Preguntas Clave del Profesor)

### P1: ¿Por qué calculaste el progreso (`progressFraction`, `remainingAmount`) en el modelo `Goal` y no en la vista?
> **Respuesta sugerida:** *"Porque responde a la regla de negocio de Clean Architecture: el cálculo de cuánto dinero falta para alcanzar una meta o qué porcentaje representa es lógica de dominio pura e independiente de la UI. De esta manera, si mañana cambiamos la vista o creamos un widget de escritorio, la regla de cálculo no se duplica ni se corrompe."*

### P2: ¿Por qué en `GoalsVM` utilizas `StateFlow` en vez de `mutableStateOf`?
> **Respuesta sugerida:** *"Porque `StateFlow` forma parte de Kotlin Coroutines y la arquitectura estándar recomendada por Google para Android. Permite desacoplar el ViewModel del runtime de Jetpack Compose, facilitando la creación de pruebas unitarias puras en JVM con `runTest` y evitando efectos colaterales de concurrencia."*

### P3: ¿Cómo se asegura que un usuario no vea las metas de otro usuario en Supabase?
> **Respuesta sugerida:** *"Por una doble capa de seguridad: en el código móvil (`GoalRepository`), la consulta Postgrest incluye `.filter { eq("user_id", userId) }`, y en el servidor de PostgreSQL aplicamos Row Level Security (RLS) con `auth.uid() = user_id`, impidiendo a nivel de base de datos que cualquier petición no autorizada acceda a filas de otros usuarios."*

### P4: ¿Cómo funciona la animación de la barra de progreso en `GoalCard`?
> **Respuesta sugerida:** *"Utilizamos la API declarativa `animateFloatAsState` de Compose. Cuando el valor de `goal.progressFraction` cambia tras un abono interactivo, Compose interpola suavemente los fotogramas durante 600 milisegundos mediante `tween`, brindando una experiencia visual fluida sin bloquear el hilo principal de la interfaz."*
