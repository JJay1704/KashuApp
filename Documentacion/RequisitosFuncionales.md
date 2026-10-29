Historias de usuario 

HU-01: Como usuario quiero registrarme e iniciar sesión con mi correo electrónico y una contraseña, para poder acceder a mi perfil de forma segura y privada desde cualquier dispositivo.

HU-02: Como usuario quiero recibir un correo de recuperación en caso de olvidar mi contraseña para poder restablecer mi acceso a la aplicación sin perder información de mis finanzas.

HU-03: Como usuario quiero registrar manualmente mis ingresos y gastos especificando monto, fecha, hora, categoría y cuenta para llevar control detallado de mi dinero.

HU-04: Como usuario quiero clasificar y renombrar mis categorías de gasto, para organizar la información financiera según mi criterio.

HU-05: Como usuario quiero editar mis transacciones de gasto e ingreso, para corregir errores de información.

HU-06: Como usuario quiero definir un presupuesto máximo mensual según categoría para evitar gastar de más en cada una de ellas.

HU-07: Como usuario quiero crear una meta de ahorro para un objetivo específico con fecha límite para visualizar cuánto me falta por alcanzar.

HU-08: Como usuario quiero ver un resumen en la pantalla de inicio con mi saldo total y patrimonio neto, para conocer mi situación financiera real de un solo vistazo.

HU-09: Como usuario quiero consultar gráficos estadísticos mensuales de mis gastos e ingresos para identificar rápidamente en qué áreas se distribuye mi dinero.

HU-10: Como usuario quiero recibir alertas cuando mi presupuesto esté por agotarse para tomar precauciones.

HU-11: Como usuario quiero ver un listado de mis operaciones recientes, para revisar rápidamente en qué he gastado y qué dinero ha ingresado recientemente sin revisar en todo el historial.

HU-12: Como usuario quiero buscar transacciones aplicando filtros según fecha, categoría, entre otros para poder localizar rápidamente movimientos antiguos y analizarlos.

Requisitos Funcionales

RF-01: El sistema debe permitir al usuario registrarse e iniciar sesión ingresando un correo electrónico válido y una contraseña segura, validando que el correo no esté registrado previamente.

RF-01.1: El sistema debe permitir registrar una nueva cuenta solicitando un correo electrónico y una contraseña, validando que el correo tenga un formato correcto y no esté registrado previamente.

RF-01.2: El sistema debe exigir que la contraseña cumpla con una política de seguridad mínima (ej. al menos 8 caracteres, una mayúscula y un número).

RF-01.3: El sistema debe permitir el inicio de sesión autenticando las credenciales ingresadas frente a la base de datos y generando una sesión activa o token de acceso seguro.

RF-01.4: El sistema debe mostrar un mensaje de error genérico (ej. "Correo o contraseña incorrectos") si los datos de acceso no coinciden, por motivos de seguridad.

RF-02: El sistema debe proveer una opción en la pantalla de inicio de sesión para solicitar el restablecimiento de contraseña mediante el correo electrónico registrado.

RF-02.1: El sistema debe permitir ingresar y guardar una nueva contraseña una vez validado el enlace de recuperación o código de verificación recibido por correo.

RF-03: El sistema debe proveer una interfaz para el registro manual seleccionando el tipo de movimiento (ingreso o gasto).

RF-03.1: El sistema debe validar e impedir el almacenamiento de la transacción si faltan campos obligatorios como el monto (numérico mayor a cero), la fecha, la hora, la categoría o la cuenta asociada.

RF-04: El sistema debe permitir al usuario crear, editar el nombre y eliminar o desactivar categorías personalizadas de gasto.

RF-04.1: El sistema debe permitir asociar un icono o color distintivo a cada categoría creada para su rápida identificación visual.

RF-05: El sistema debe permitir seleccionar cualquier transacción existente del historial y modificar sus atributos (monto, fecha, hora, categoría o cuenta).

RF-05.1: El sistema debe recalcular automáticamente los saldos de las cuentas afectadas de forma inmediata tras guardar los cambios de una transacción editada.

RF-06: El sistema debe permitir al usuario asignar un monto límite monetario a una categoría específica para un período mensual determinado.

RF-06.1: El sistema debe reiniciar o evaluar el estado de los presupuestos de forma automática al cambiar de período mensual.

RF-07: El sistema debe permitir registrar una meta especificando nombre del objetivo, monto meta y fecha límite de cumplimiento, así como su edición o eliminación.

RF-07.1: El sistema debe calcular y mostrar de manera gráfica el porcentaje de avance, el monto acumulado y la diferencia monetaria faltante para llegar a la meta.

RF-07.2: El sistema debe permitir registrar aportes a una meta deduciendo el monto desde una cuenta activa seleccionada y emitir una alerta visual cuando la fecha límite de cumplimiento esté próxima a vencer.

RF-08: El sistema debe sumar el saldo actual de todas las cuentas activas registradas por el usuario para mostrar el saldo total consolidado.

RF-08.1: El sistema debe calcular el patrimonio neto restando las deudas o pasivos (si aplican) de los activos totales en tiempo real al abrir la pantalla principal.

RF-08.2: El sistema debe permitir crear, editar y activar o desactivar cuentas (clasificadas como efectivo, banco, tarjeta u otras) con saldo inicial, así como registrar pasivos o deudas para alimentar el cálculo del patrimonio neto con datos reales.

RF-09: El sistema debe procesar las transacciones del mes en curso y renderizar gráficos estadísticos (tipo pastel o barras) desglosados por categoría.

RF-09.1: El sistema debe incluir visualizaciones separadas o conjuntas que permitan contrastar el total de ingresos frente al total de gastos del período seleccionado.

RF-10: El sistema debe evaluar de forma automática el acumulado de gastos frente al presupuesto configurado por categoría.

RF-10.1: El sistema debe enviar una notificación push o de alerta interna al dispositivo cuando el gasto alcance un porcentaje crítico (por ejemplo, el 80% o el 100%) del presupuesto límite.

RF-11: El sistema debe consultar y renderizar en la pantalla de inicio un segmento limitado (por ejemplo, las últimas 5 o 10 operaciones) ordenadas de la más reciente a la más antigua.

RF-11.1: Cada elemento del listado reciente debe mostrar claramente el icono y color distintivo de su categoría, la descripción breve, el signo y color diferenciador para ingresos (+) o gastos (-) y el monto.

RF-12: El sistema debe proveer controles interactivos en la vista de historial para filtrar transacciones por rango de fechas específico, tipo de operación y categoría.

RF-12.1: El sistema debe actualizar el listado de resultados de manera dinámica conforme el usuario aplique o modifique los criterios de filtrado.
