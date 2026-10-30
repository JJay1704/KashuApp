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
│       ├── guia-codigo/            # Skill /guia-codigo para guiar la construcción paso a paso
│       ├── preguntasSustentacion/  # Skill /sustentacion para simular evaluaciones orales rigurosas
│       └── reto-codigo/            # Skill /reto-codigo para retos prácticos de modificación en vivo
├── App/                            # Proyecto Android Studio (Gradle)
│   ├── app/
│   │   ├── src/main/java/com/kashuapp/
│   │   │   ├── core/               # Componentes reutilizables y navegación global
│   │   │   │   ├── composables/    # Componentes genéricos compartidos (GenericComposables.kt)
│   │   │   │   └── navigation/     # NavHost central, rutas y tabs (Navigation.kt, HomeTab.kt, MenuTab.kt)
│   │   │   ├── data/               # Repositorios, modelos y cliente backend (Supabase)
│   │   │   │   ├── KashuSupaBase.kt # Cliente singleton de Supabase
│   │   │   │   ├── account/        # Modelos y repositorio de cuentas (Account, AccountRepository, IAccountRepository)
│   │   │   │   ├── accounType/     # Tipos de cuenta bancaria/efectivo (AccountType, IAccountTypeRepository)
│   │   │   │   ├── auth/           # Autenticación y perfil (AuthRepository, IAuthRepository, UserProfile)
│   │   │   │   ├── budget/         # Presupuestos mensuales (Budget, BudgetRepository, IBudgetRepository)
│   │   │   │   ├── category/       # Categorías de transacción (Category, CategoryRepository, ICategoryRepository)
│   │   │   │   ├── currency/       # Monedas soportadas (Currency, CurrencyRepository, ICurrencyRepository)
│   │   │   │   ├── goal/           # Metas de ahorro y aportes (Goal, GoalRepository, IGoalRepository)
│   │   │   │   └── transaction/    # Ingresos y gastos (Transaction, TransactionRepository, ITransactionRepository)
│   │   │   ├── ui/                 # Vistas, estados y ViewModels organizados por pantalla/módulo
│   │   │   │   ├── account/        # Gestión de cuentas y pasivos (AccountScreen, AccountForm, AccountVM)
│   │   │   │   ├── category/       # Catálogo de categorías (CategoryScreen, CategoryForm, CategoryVM)
│   │   │   │   ├── goals/          # Metas de ahorro (form/ y list/, GoalScreen, GoalsVM, GoalFormVM)
│   │   │   │   ├── home/           # Dashboard principal, saldo y resumen (HomeScreen, HomeVM)
│   │   │   │   ├── login/          # Inicio de sesión (LoginScreen, LoginVM, LoginState)
│   │   │   │   ├── menu/           # Menú lateral / secundario de módulos (MenuScreen, MenuVM)
│   │   │   │   ├── resetPswd/      # Recuperación de contraseña por correo y OTP (ResetPswdScreen, ResetPswdVM)
│   │   │   │   ├── signUp/         # Registro de nuevos usuarios (SignUpScreen, SignUpVM, SignUpState)
│   │   │   │   ├── theme/          # Sistema de diseño (Color.kt, Theme.kt, Type.kt, KashuScheme.kt)
│   │   │   │   └── transaction/    # Historial y registro (form/ y list/, TransactionScreen, TransactionVM)
│   │   │   └── MainActivity.kt     # Punto de entrada de la actividad y NavHost
│   │   └── build.gradle.kts        # Configuración y dependencias del módulo app
│   ├── gradle/libs.versions.toml   # Catálogo centralizado de versiones (Version Catalog)
│   ├── build.gradle.kts            # Configuración raíz de Gradle
│   └── gradlew / gradlew.bat       # Wrapper de Gradle
├── Documentacion/                  # Especificaciones funcionales y diseño
│   ├── RequisitosFuncionales.md    # Historias de usuario (HU-01 a HU-12) y requisitos (RF-01 a RF-12.1)
│   ├── BaseDeDatos.md              # Esquema de base de datos Supabase, tablas, RLS y triggers ACID
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
| **Backend & Base de Datos** | Supabase (Supabase BOM `3.1.4`, Auth, Postgrest Database con PostgreSQL + RLS, Ktor OkHttp). **Nota de Arquitectura:** El cálculo y actualización de `current_balance` en `account` está delegado a un Trigger en PostgreSQL (Supabase) para garantizar consistencia transaccional ACID. La app móvil únicamente refresca las cuentas tras operaciones sobre transacciones. |
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
  - **Decisión de Diseño (Home vs. Historial):** En la pantalla de inicio (`HomeScreen` / `HomeView`), el listado de actividad reciente (**HU-11 / RF-11**) es **estrictamente de solo lectura (informativo)**. La edición y eliminación de transacciones (**HU-05 / RF-05**) se realiza única y exclusivamente desde la vista completa de historial (`History` / `TransactionScreen`), manteniendo separación de responsabilidades entre el resumen ejecutivo y la gestión operativa.
  - **Decisión de Diseño (Visualización en `TransactionItemCard` - Actualizado para T3):** Para cumplir con **HU-11 / RF-11**, la tarjeta de transacción (`TransactionItemCard`) debe mostrar:
    1. El **ícono y color distintivo de su categoría** (o color/ícono de ingresos/gastos si no tiene categoría).
    2. El **nombre de la categoría** y la **descripción del movimiento**.
    3. El nombre de la cuenta emisora/receptora.
    4. El **monto con signo explícito** (`+` para ingresos en verde, `-` para gastos en rojo).
- **Categorías (HU-04 / RF-04):** Clasificación de gastos, creación, edición, icono y color personalizable.
- **Presupuestos y Alertas (HU-06, HU-10 / RF-06, RF-10):** Asignación de tope monetario mensual por categoría, cálculo de porcentaje consumido y alertas al alcanzar 80% o 100%.
- **Metas de Ahorro (HU-07 / RF-07):** Registro de objetivo, monto meta, fecha límite y cálculo visual de avance y saldo faltante.
- **Dashboard y Estadísticas (HU-08, HU-09 / RF-08, RF-09):** Saldo total consolidado, patrimonio neto (activos - pasivos) y gráficos estadísticos (gastos e ingresos por categoría).

---

## 7. Instrucciones Específicas para Sustentaciones y Práctica Académica

1. **Habilidades de Evaluación y Guía disponibles en `.agents/skills/`:**
   - **`/sustentacion` (`preguntasSustentacion/SKILL.md`):** Simula una sustentación oral rigurosa (ubicación quirúrgica en el código -> comportamientos observables y flujo entre capas -> líneas puntuales -> preguntas contrafactuales *"¿qué se rompe si...?"*).
   - **`/reto-codigo` (`reto-codigo/SKILL.md` + `ejercicios.md`):** Simula la prueba práctica de modificación en vivo que pide el profesor durante la evaluación (plantear un cambio en términos de pantalla/comportamiento sin mencionar archivos `.kt` y auditar la solución del estudiante con rigor).
   - **`/guia-codigo` (`guia-codigo/SKILL.md`):** Guía al estudiante paso a paso para construir una funcionalidad por sí mismo sin escribir el código en sus archivos, verificando arquitectura MVVM + State Hoisting en cada paso.
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

---

## 10. Protocolo de Verificación para Entrega T3 (Checklist Mandatorio)

Cuando el usuario pregunte *"¿el proyecto está listo para el T3?"*, *"¿estamos listos para la entrega 3?"*, *"verifica el T3"* o cualquier variante similar, el agente **DEBE activar inmediatamente una auditoría estricta del código fuente** contra el alcance oficial del T3.

### 10.1. Alcance y Criterios de Aceptación del T3
El agente debe evaluar punto por punto los siguientes requerimientos:

1. **HU-02 / RF-02: Recuperación de Contraseña**
   - **Acción:** Desde la pantalla de login, el usuario presiona "¿Olvidaste tu contraseña?", ingresa su correo registrado y solicita el restablecimiento. Recibe un correo real con enlace o código OTP.
   - **Resultado esperado:** El usuario ingresa la nueva contraseña (validando la política mínima de seguridad) y se actualiza su acceso en Supabase Auth permitiéndole iniciar sesión con la nueva credencial.

2. **Gestión de Cuentas y Deudas (Impacto en HU-08 / RF-08, RF-08.1)**
   - **Acción:** El usuario crea, edita y activa/desactiva cuentas (efectivo, banco, tarjeta) especificando saldo inicial. Asimismo, se gestionan pasivos/deudas.
   - **Resultado esperado:** 
     - Las cuentas desactivadas no se computan en saldos operativos ni en el saldo consolidado activo.
     - El saldo consolidado suma activos netos disponibles.
     - El **patrimonio neto** calcula con datos reales la fórmula financiera: **Activos Totales - Pasivos / Deudas Totales**.

3. **HU-07 / RF-07: Creación y Gestión de Metas de Ahorro**
   - **Acción:** El usuario registra metas con nombre, monto meta y fecha límite. Puede editarlas y eliminarlas de forma persistente.
   - **Resultado esperado:** La meta persiste en Supabase, refleja validaciones numéricas (> 0) y permite edición y eliminación limpia.

4. **RF-07.1: Avance de la Meta de Ahorro y Alertas**
   - **Acción:** El usuario registra abonos/aportes hacia una meta **seleccionando una cuenta de origen**.
   - **Resultado esperado:**
     - El monto abonado se descuenta del balance de la cuenta seleccionada y se suma al acumulado de la meta.
     - La vista muestra el gráfico/barra de porcentaje de avance, el monto acumulado y **el saldo faltante para completar la meta**.
     - Se muestra un aviso/indicador visual si la fecha límite está cerca (o vencida).

5. **HU-11 / RF-11: Operaciones Recientes en la Pantalla de Inicio**
   - **Acción:** El usuario visualiza la pantalla de inicio tras registrar movimientos.
   - **Resultado esperado:** Listado de las últimas operaciones ordenadas de la más reciente a la más antigua (cronológico descendente). Cada elemento muestra:
     - Ícono y color distintivo de su categoría (RF-04.1).
     - Descripción del movimiento.
     - Nombre de la cuenta.
     - Monto formateado con signo explícito (`+` verde para ingresos, `-` rojo para gastos).

### 10.2. Formato del Reporte de Verificación T3
El reporte debe seguir la estructura:
- **Tabla de Estado Funcional T3 (HU / RF / Cuentas y Deudas)**
- **Tabla de Auditoría Técnica (MVVM, State Hoisting, Supabase RLS y SOLID)**
- **Análisis de Gaps y Pendientes Críticos**
- **Veredicto Final Riguroso: "LISTO PARA T3" o "NO ESTÁ LISTO PARA T3"**

---

## 11. Conocimiento del Proyecto: Estado de Cumplimiento

**Fecha de revisión: 29 de octubre de 2026.** Revisión estática del código local, incluidos cambios sin commit. Esta tabla es una fotografía del estado revisado, no reemplaza los requisitos ni constituye una certificación de entrega. Debe actualizarse tras implementar y verificar cambios. Los estados de GitHub Projects no prueban cumplimiento funcional.

**Alcance de la evidencia:** «Cumple» significa implementación encontrada en el código. No se ejecutó la app ni se verificaron correos reales, persistencia real, políticas RLS o triggers de Supabase. Las funcionalidades dependientes del backend necesitan pruebas de extremo a extremo antes de darse por cerradas.

| Estado | Requisito | Resultado de la revisión |
| :--- | :--- | :--- |
| Cumple | RF-01.2: Contraseña segura | Valida ocho caracteres, mayúscula y número. |
| Cumple | RF-01.4: Error genérico de acceso | Muestra «Correo o contraseña incorrectos». |
| Cumple | RF-02: Solicitar recuperación desde login | La opción abre el flujo y solicita el correo. |
| Cumple | RF-03: Seleccionar ingreso o gasto | Ambos tipos están disponibles. |
| Cumple | RF-04: Gestionar categorías | Creación, cambio de nombre y eliminación implementados. |
| Cumple | RF-05: Editar transacciones | Modificación de movimientos desde el historial. |
| Cumple | RF-06: Presupuesto mensual por categoría | Asigna límites por categoría, mes y año. |
| Cumple | RF-07: Gestionar metas | Creación, edición y eliminación con nombre, monto objetivo y fecha. |
| Cumple | RF-07.1: Mostrar avance de metas | Muestra barra, porcentaje, acumulado y faltante por meta. El aporte desde una cuenta se evalúa por separado. |
| Cumple | RF-08: Consolidar saldos activos | Suma cuentas activas, separando resultados por moneda. No equivale a patrimonio neto. |
| Cumple | RF-11: Listar operaciones recientes | Orden descendente por fecha y límite de cinco movimientos. |
| Cumple | RF-12.1: Actualizar resultados al filtrar | Cambiar o limpiar filtros consulta y actualiza la lista. |
| Parcial | RF-01 / RF-01.1: Registro y correo válido | Existe el registro, pero la validación local solo comprueba `@`. Falta probar correo duplicado. |
| Parcial | RF-01.3: Autenticación y sesión | Flujo implementado con Supabase; falta comprobar su funcionamiento real. |
| Parcial | HU-02 / RF-02.1: Recuperación completa | Existe solicitud de correo, validación OTP y actualización de contraseña; falta probar recepción y acceso con la nueva clave. |
| Parcial | RF-03.1: Validación de transacciones | Valida monto, cuenta y categoría; no valida explícitamente fecha y hora al guardar. |
| Parcial | HU-03 / RF-05.1: Impacto en saldos | Registra, edita y refresca cuentas; falta verificar el trigger que actualiza balances. |
| Parcial | RF-06.1: Cambio automático de mes | Evalúa períodos seleccionados; no detecta el cambio de mes con el ViewModel activo. |
| Parcial | RF-07.2: Aportes y alerta de vencimiento | Hay alerta visual; el aporte aumenta la meta sin seleccionar ni descontar una cuenta de origen. |
| Parcial | HU-08: Saldo y patrimonio | Saldo implementado; falta patrimonio neto. |
| Parcial | RF-08.2: Cuentas y deudas | Existe creación y edición de cuentas; falta completar activación/desactivación y gestión de pasivos. |
| Parcial | RF-09 / RF-09.1: Estadísticas | Hay gráficos por categoría y comparación ingreso/gasto, pero suma monedas distintas y presenta resultados como soles. |
| Parcial | RF-10 / RF-10.1: Consumo y alertas | Calcula consumo y muestra avisos al 80% y 100% en Presupuestos; mezcla monedas y no garantiza avisar al registrar un gasto fuera de esa pantalla. |
| Parcial | HU-11 / RF-11.1: Detalle de operaciones recientes | Muestra descripción, cuenta, monto, signo y color de ingreso/gasto; falta ícono y color de categoría. |
| Parcial | HU-12 / RF-12: Filtros del historial | Cuenta, categoría, tipo y períodos predefinidos implementados; falta rango de fechas personalizado. |
| No cumple | RF-04.1: Personalizar ícono y color | El formulario no permite escogerlos; guarda valores fijos. |
| No cumple | RF-08.1: Patrimonio neto | No calcula activos menos deudas. La etiqueta «Net Worth» muestra una suma de saldos. |

### 11.1. Evidencia Principal

- Autenticación: `SignUpVM.kt`, `LoginVM.kt`, `ResetPswdVM.kt` y `AuthRepository.kt`.
- Inicio y saldo: `HomeVM.kt`, `HomeComposables.kt` y `GenericComposables.kt` (`NetWorthCard`).
- Transacciones y filtros: `TransactionFormVM.kt`, `TransactionVM.kt`, `TransactionScreen.kt`, `TransactionComposables.kt` y `TransactionRepository.kt`.
- Categorías: `CategoryVM.kt`, `CategoryForm.kt` y `CategoryRepository.kt`. La creación fija `icon = "category"` y `color = "#34D399"`.
- Metas: `GoalFormVM.kt`, `GoalsVM.kt`, `GoalComposables.kt` y `GoalRepository.kt`. `submitSavings` solo actualiza la meta.
- Cuentas: `AccountFormVM.kt`, `AccountVM.kt` y `Account.kt`.
- Presupuestos y estadísticas: `BudgetVM.kt`, `BudgetComposables.kt`, `AnalyticsVM.kt` y `AnalyticsComposables.kt`.

### 11.2. Pendientes Prioritarios y Verificación

1. Implementar gestión de pasivos y patrimonio neto real.
2. Completar aportes a metas con cuenta activa de origen y descuento consistente del saldo.
3. Permitir personalizar ícono/color de categorías y mostrarlos en las tarjetas de transacción.
4. Separar por moneda o convertir explícitamente antes de sumar en presupuestos, estadísticas y resúmenes globales de metas.
5. Fortalecer validación de correo, fecha y hora; completar rango de fechas y cambio de período.
6. Probar recuperación con correo real, OTP y acceso con la nueva contraseña.
7. Verificar persistencia, aislamiento por usuario/RLS y triggers de saldos al crear, editar y eliminar movimientos.

El diagrama de flujo existe en `Documentacion/DiagramaDeFlujo.pdf`, pero su contenido no fue auditado. No se realizó en esta revisión la auditoría técnica completa exigida para emitir un veredicto formal T1/T3.


