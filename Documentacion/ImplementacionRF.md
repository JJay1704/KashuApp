# Implementación de requisitos funcionales

Rama de trabajo: `codex/completar-requisitos-funcionales`.

## Alcance y estado

Los RF pendientes tienen implementación preparada en esta rama. Las pruebas funcionales finales las realiza el usuario. No se ha modificado ni integrado la rama `main`, ni se ha aplicado el SQL al Supabase compartido. No marcar esta entrega como validada en un dispositivo o en Supabase hasta completar esas pruebas.

| RF | Implementación |
| --- | --- |
| RF-01 a RF-03.1 | Se conserva el registro, sesión y recuperación verificados por el usuario. Fecha y hora mantienen los valores de apertura del formulario. Los montos nuevos exigen un valor finito positivo con hasta dos decimales. |
| RF-04 / RF-04.1 | Creación, edición y eliminación de categorías; selección persistente de ícono y color; visualización en el catálogo y tarjetas de movimientos. |
| RF-05 / RF-05.1 | Edición desde historial y refresco de cuentas. El SQL mantiene el trigger de balance y distingue activos de pasivos. |
| RF-06 / RF-06.1 | Presupuestos por categoría, mes, año y moneda; cálculo por intervalo mensual completo y actualización al cambiar de mes. No se arrastran límites automáticamente de un mes a otro. |
| RF-07 | Crear, editar y eliminar metas; se permite conservar una fecha vencida al editar; la moneda y el acumulado no se sobrescriben al editar. |
| RF-07.1 | Barra, porcentaje, acumulado y faltante por meta; resumen global separado por moneda. |
| RF-07.2 | Cuenta de origen activa, de tipo activo y de la misma moneda. RPC y trigger validan saldo y registran descuento/abono de forma atómica; identificador de solicitud para reintentos. Alertas a siete días del vencimiento y para metas vencidas. |
| RF-08 | Saldos disponibles de cuentas activas separados por moneda. |
| RF-08.1 | Patrimonio por moneda: activos disponibles + ahorro reservado en metas − pasivos activos. Reservar dinero en una meta no destruye patrimonio ni se contabiliza como gasto. |
| RF-08.2 | Alta y edición de activos y deudas mediante `account_type.classification`; activación/desactivación persistente. Una deuda usa saldo positivo para el monto pendiente. |
| RF-09 / RF-09.1 | Gráficos de distribución y comparación filtrados por moneda. No hay conversión implícita entre monedas. |
| RF-10 / RF-10.1 | Consumo por categoría y moneda. Al guardar un gasto se comprueba el cruce del 80% o 100% y se muestra una alerta interna. Si la consulta de alertas falla, se informa que el movimiento sí se guardó. |
| RF-11 / RF-11.1 | Últimos cinco movimientos por instante descendente, categoría con ícono/color, descripción, cuenta y monto con signo/color. Inicio sigue siendo informativo. |
| RF-12 / RF-12.1 | Filtros combinados por cuenta, categoría, tipo y fechas; rango personalizado Desde/Hasta; consultas anteriores canceladas y respuestas obsoletas descartadas. |

## Preparar un backend aislado

1. Utilizar un proyecto o rama de desarrollo de Supabase con el esquema base de KashuApp, Auth y catálogos `currency` / `account_type`. Este repositorio no incluye una copia completa del esquema de producción.
2. Revisar y ejecutar `sql/completar_rf.sql` **solo en ese entorno**. Es un script de despliegue transaccional, no una migración ya aplicada. El índice de presupuestos requiere que no existan duplicados por usuario, categoría, año, mes y moneda; si los hay, el script falla y revierte sin eliminarlos.
3. La configuración de Gradle y `MainActivity.kt` se conservan como en `main`, por solicitud del usuario. La app utiliza `SUPABASE_URL` y `SUPABASE_KEY` de `App/local.properties`, sin propiedades adicionales ni cambios de identificador. No se modificó ese archivo de propiedades. Para apuntar a un entorno aislado, el usuario puede cambiar su configuración local habitual. La rama Git no aísla la base de datos. No colocar claves `service_role` en la aplicación.

## Decisiones financieras

- Los catálogos de tipos de cuenta se reutilizan; no se duplican por usuario. El script agrega el tipo `deuda` si aún no existe.
- En un activo, un ingreso aumenta el balance y un gasto lo disminuye. En un pasivo, un gasto aumenta la deuda y un ingreso representa un pago y la reduce. No se permiten pasivos negativos.
- Cambiar el saldo inicial ajusta el balance por la diferencia, bajo bloqueo de fila. Editar nombre o estado no reenvía ni sobrescribe un saldo actual potencialmente obsoleto.
- No se puede cambiar tipo o moneda de una cuenta con transacciones o aportes: reinterpretaría el historial. Crear otra cuenta es la alternativa.
- El ahorro previo declarado al crear una meta se considera dinero ya reservado fuera de las cuentas; no genera una segunda deducción. Para dinero que está en una cuenta, dejarlo en cero y registrar un aporte.
- Eliminar una meta devuelve los aportes de su historial a cada cuenta de origen, aunque esté inactiva. El ahorro previo declarado no se devuelve a ninguna cuenta porque nunca salió de una.
- Las metas existentes anteriores a este script no tienen historial de aportes reconstruido; su acumulado se trata como ahorro previo. No se inventan movimientos históricos.
- Los límites de fechas son intervalos `[inicio, día siguiente al final)`, con zona horaria local explícita. Así se incluye el último día completo.
- Los presupuestos anteriores reciben moneda `PEN`; comprobar que esa interpretación coincide con los datos existentes antes de desplegar.

## Pruebas a cargo del usuario

1. Crear y editar categoría con ícono/color; comprobar Inicio e Historial.
2. Combinar filtros, limpiar filtros y probar un rango invertido y movimientos al final del último día.
3. Crear un activo y una deuda; desactivar/reactivar cuentas y comprobar saldo/patrimonio y selectores.
4. Registrar, editar, mover entre cuentas y eliminar movimientos en activos y pasivos.
5. Probar dos monedas con la misma categoría: presupuestos y gráficos deben mantenerse separados.
6. Registrar gastos que crucen 80% y 100%, también mediante edición; comprobar el aviso interno.
7. Cambiar de período mensual y comprobar que no se acumulan gastos de otros meses.
8. Editar una meta vencida; aportar desde una cuenta válida; rechazar cuenta inactiva, pasivo, moneda distinta y saldo insuficiente.
9. Reintentar un mismo aporte y comprobar que no cobra dos veces. Probar aportes concurrentes con saldo limitado.
10. Eliminar una meta y comprobar devoluciones; verificar acceso con dos usuarios sin cruces de datos.

El usuario realizará las pruebas funcionales del proyecto Android. No se harán pruebas SQL ni cambios en el backend compartido. El script `sql/completar_rf.sql` queda únicamente como referencia de las dependencias de backend; no ha sido aplicado.

La ejecución de `assembleDebug`, `testDebugUnitTest` y `lintDebug`, iniciada antes de que el usuario asumiera las pruebas, finalizó correctamente. Hay advertencias de lint. Después se hicieron ajustes menores de revisión de código que no se volvieron a ejecutar por indicación del usuario. Se conservan las pruebas unitarias de reglas financieras en `FinanceRulesTest.kt` para su uso opcional.

## Dependencias al probar la app

Los selectores, categorías, filtros y cálculos están implementados en Android. Para probar el flujo completo con persistencia, el backend de desarrollo debe exponer `budget.currency`, `contribute_to_goal` y `delete_goal_with_refund`, además del tratamiento de balances de pasivos y ajustes de saldo inicial. Sin ese soporte, los aportes/devoluciones y presupuestos por moneda no deben considerarse operativos de extremo a extremo, aunque el código Android esté preparado.
