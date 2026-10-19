# Catálogo de Retos para KashuApp (Modo Profesor Riguroso)

Referencia para el Paso 0 de `/reto-codigo`. Cada categoría incluye pedidos de ejemplo adaptados al dominio real de **KashuApp** (redactados como los pide el profesor en vivo: hablando de pantallas y comportamiento, sin mencionar archivos `.kt` ni clases) y los **criterios internos de auditoría** que usas para evaluar (no se muestran al estudiante al plantear el reto).

> **Regla de oro antes de elegir:** Inspecciona siempre el código actual de `App/` para verificar qué retos ya están implementados y cuáles no. Nunca pidas algo que ya esté hecho.

## Niveles de Exigencia

- **Nivel 1 — Puntual (UI / Validación en 1 módulo):** Un cambio localizado en un formulario, tarjeta o listado.
- **Nivel 2 — Flujo Completo (UI → ViewModel → Estado / Repositorio):** Un cambio que exige modificar estado, lógica en el `ViewModel` y su representación reactiva en Compose.
- **Nivel 3 — Modificación de Sustentación (Casos borde y cruce de lógica):** El clásico cambio que pide el profesor para comprobar si el alumno domina el flujo de datos y `StateFlow` o si solo generó el proyecto con IA.

---

## 1. Validaciones de Negocio en Formularios · Nivel 1 → 2

Pedidos de ejemplo:
- *"En la pantalla de presupuestos, no permitas registrar dos presupuestos para la misma categoría en el mismo mes y año; muestra un error claro debajo del selector de categoría."*
- *"En la ventana para abonar dinero a una meta de ahorro, si el usuario presiona confirmar con el campo vacío, en cero o con letras, muestra un mensaje de error en rojo debajo del campo y no cierres la ventana."*
- *"Al crear o editar una cuenta bancaria, no permitas guardar otra cuenta con exactamente el mismo nombre (sin importar mayúsculas o minúsculas) y muestra el error en el campo de nombre."*
- *"En el registro de usuario, exige que la contraseña tenga al menos una letra mayúscula y un número además de los 8 caracteres; si no cumple, muestra el motivo exacto."*
- *"En el formulario de transacciones, impide registrar un monto mayor a 1,000,000 y muestra un mensaje indicando el tope permitido."*

Criterios internos de evaluación:
- La validación vive en el `ViewModel` (o en el estado del diálogo si es local) y actualiza el `StateFlow` inmutable con `.copy(...)`.
- El mensaje de error es visible en la UI (`isError` / `errorMessage` o `labelError`).
- Con datos inválidos **no** se invoca al repositorio ni se cierra el formulario.
- Al volver a escribir o cambiar la selección, el error desaparece inmediatamente.

---

## 2. Cálculos e Indicadores Visuales en Tarjetas · Nivel 1 → 2

Pedidos de ejemplo:
- *"En cada tarjeta de meta de ahorro, muestra cuánto dinero falta exactamente para alcanzar el objetivo (ej. 'Faltan: PEN 250.00'), y si ya superó o alcanzó la meta, muestra '¡Meta alcanzada!'."*
- *"En el encabezado de la lista de cuentas, muestra el conteo total de cuentas activas que tiene el usuario."*
- *"En las tarjetas de presupuesto, cuando una categoría supere el 100% del límite, muestra además por cuánto dinero se excedió (ej. 'Excedido en S/. 45.00')."*
- *"En la pantalla de estadísticas (Analytics), muestra junto al total de gastos e ingresos el balance neto del mes (Ingresos menos Gastos) pintado de verde si es positivo o rojo si es negativo."*

Criterios internos de evaluación:
- El cálculo maneja correctamente decimales (`%.2f`) y evita números negativos absurdos (ej. usando `coerceAtLeast(0.0)` cuando corresponde).
- No rompe el diseño de la tarjeta ni desborda textos en pantallas pequeñas.
- Se actualiza reactivamente al crear, editar o abonar sin necesidad de reiniciar la app.

---

## 3. Navegación, Filtros y Comportamiento de Listas · Nivel 2

Pedidos de ejemplo:
- *"En el historial de transacciones, agrega un buscador o filtro rápido por tipo de movimiento (Todos, Solo Ingresos, Solo Gastos) que funcione en conjunto con los filtros actuales."*
- *"En la pantalla de metas de ahorro, agrega una opción o filtro para ver 'Todas', 'En progreso' o 'Completadas'."*
- *"En la pantalla de categorías, separa o permite filtrar visualmente las categorías de Ingreso y las de Gasto."*
- *"Cuando se cierre el formulario de crear o editar una cuenta, asegúrate de que la lista de cuentas se actualice automáticamente sin tener que salir y volver a entrar a la pantalla."*

Criterios internos de evaluación:
- El estado del filtro vive en el `State` / `ViewModel`, no como una variable suelta que se pierde o desincroniza.
- Al limpiar el filtro vuelven a mostrarse todos los registros sin perder datos.
- La navegación hacia atrás usa `popBackStack()` sin duplicar pantallas en la pila de navegación.

---

## 4. Confirmaciones, Seguridad de Acciones y Estados de Carga · Nivel 1 → 2

Pedidos de ejemplo:
- *"Antes de eliminar una cuenta o una categoría, muestra un cuadro de diálogo de confirmación preguntando '¿Estás seguro de eliminar X?' con botones Cancelar y Eliminar."*
- *"En la pantalla de inicio de sesión, si el usuario intenta entrar más de 3 veces seguidas con credenciales erróneas, muestra una advertencia sugiriendo recuperar su contraseña."*
- *"Cuando una lista (en Historial, Metas o Cuentas) no tenga ningún registro, muestra un estado vacío claro con un botón directo para crear el primer registro."*

Criterios internos de evaluación:
- Si el usuario cancela el diálogo de confirmación, no se ejecuta el borrado en Supabase.
- Los botones de envío se deshabilitan mientras `isLoading` es `true` para evitar doble inserción.

---

## 5. Retos Nivel 3 (Prueba de Fuego del Profesor en Sustentación) · Nivel 3

Pedidos de ejemplo:
- *"En el formulario de transacciones, cuando el usuario esté registrando un GASTO, verifica si el monto ingresado supera el saldo actual (`currentBalance`) de la cuenta seleccionada; si lo supera, no permitas guardar y muestra 'Saldo insuficiente en la cuenta seleccionada (Disponible: X)'."*
- *"En la ventana de abonar a una meta de ahorro, agrega un botón rápido de '+10%' que sume automáticamente el 10% del monto objetivo de esa meta en el campo de abono."*
- *"Agrega un campo de 'Nota o referencia opcional' a las metas de ahorro que se guarde en el estado/modelo, se pueda editar y se muestre en la tarjeta solo si no está vacío."*

Criterios internos de evaluación:
- El estudiante logra cruzar estados dentro del `ViewModel` (ej. buscar la cuenta seleccionada en `uiState.value.accounts` y comparar `amount > account.currentBalance` solo cuando `selectedTypeTrans == "EXPENSE"`).
- No rompe la edición de transacciones ni otras pantallas.
- Puede explicar en 2 frases cómo fluye el dato desde la UI hasta la validación en el `ViewModel`.
