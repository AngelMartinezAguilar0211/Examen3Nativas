# 🌐 Examen3Nativas - Navegador Offline vía Bluetooth (Cliente-Servidor)

Este proyecto Android implementa una solución de navegación web **sin conexión a Internet directa** en el dispositivo cliente. Utiliza un **modelo cliente-servidor basado en Bluetooth**, donde:

- 📡 El **servidor** (dispositivo con Internet) actúa como proxy web.
- 📱 El **cliente** (dispositivo sin Internet) envía URLs y recibe contenido web renderizado.

---

## 📦 Estructura del Proyecto

El sistema se divide en **dos aplicaciones independientes**:

### 🔷 Servidor
- Se conecta a Internet.
- Espera conexiones Bluetooth entrantes.
- Recibe URLs del cliente.
- Hace la petición HTTP real.
- Comprime la respuesta (GZIP) y la envía de vuelta.

Archivos clave:
- `MainActivity.kt`
- `BluetoothServerService.kt`
- `HttpProxyHandler.kt`

### 🔶 Cliente
- No tiene conexión a Internet.
- Muestra una interfaz de navegador simplificada.
- Envía solicitudes web al servidor por Bluetooth.
- Descomprime el HTML recibido y lo muestra en WebView.

Archivos clave:
- `ClientActivity.kt`
- `BluetoothClientService.kt`
- `DeviceSelectActivity.kt`
- `WebContentReceiver.kt`

---

## 🧪 Funcionalidades Soportadas

| Función | Descripción |
|--------|-------------|
| 🔵 Comunicación Bluetooth | Cliente y servidor se comunican usando Sockets Bluetooth |
| 🌍 Navegación Web | El cliente puede acceder a contenido web a través del servidor |
| 🧠 Historial | Guarda sitios visitados (excepto en modo incógnito) |
| 👤 Modo incógnito | Navegación sin historial |
| 📉 Ahorro de datos | Elimina imágenes del contenido HTML |
| 🔘 UI adaptable | Controles como "Ir", "Atrás", "Siguiente" pueden ocultarse |

---

## 📲 Ejecución y Pruebas

### Requisitos:
- 2 dispositivos Android físicos con API 24+
- Ambos deben estar emparejados por Bluetooth desde configuración
- Servidor con acceso a Internet

### Pasos:

1. **Instalar la app Servidor** en el dispositivo con Internet.
2. **Ejecutar la app Cliente** en el dispositivo sin Internet.
3. En el Cliente, seleccionar el dispositivo Servidor desde la lista Bluetooth.
4. Escribir una URL (ej. `https://www.google.com`) y presionar "Ir".
5. El cliente renderiza el contenido recibido.

---

## 🛑 Consideraciones

- El cliente **no debe tener el permiso de Internet** (está restringido por diseño).
- Algunos sitios HTTPS pueden fallar por certificados no confiables (ej. `escom.ipn.mx`).
- Se requiere pedir permisos Bluetooth (`BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`) en tiempo de ejecución para Android 12+.
- El sistema **no es seguro para producción**; no implementa cifrado, autenticación ni validación de certificados.

---

## 🎨 Temas y UI

- Modo claro: color **guinda**
- Modo oscuro: color **azul**
- Diseño con `ImageButton`s y barra de navegación simplificada.

---

## 🧠 Autores y créditos

- Proyecto realizado por Sharon Anette Navarrete Becerril como parte del Examen 3 del curso de **Desarrollo de Aplicaciones Móviles Nativas**.
- Asistencia técnica y documentación generada con ayuda de IA (ChatGPT, OpenAI).

---

## 🛠️ Licencia

Este proyecto es únicamente con fines académicos y no debe utilizarse en aplicaciones comerciales sin revisión de seguridad ni permisos adecuados.
