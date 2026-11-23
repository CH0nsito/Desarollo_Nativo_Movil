# Práctica 15 - Notificaciones Locales y Push Notifications

Aplicación Android en Kotlin con Jetpack Compose para la materia Desarrollo Móvil Nativo.
Autor: Sean Josué Muro López - Ingeniería de Software, FIM-UAS, Grupo 301.

## Descripción

La app tiene dos partes:

- **Parte A (notificaciones locales):** un botón dispara una notificación usando NotificationManager y un NotificationChannel. La notificación lleva un PendingIntent que abre la pantalla de destino de la app al tocarla.
- **Parte B (Firebase Cloud Messaging):** la app solicita el permiso POST_NOTIFICATIONS (Android 13+), obtiene el FCM Token y lo muestra en pantalla. Recibe notificaciones push desde Firebase Console, procesa el Data Payload y navega a la pantalla indicada mediante un Deep Link.

## Estructura

| Archivo | Función |
|---|---|
| `NotificacionesUtils.kt` | Crea el canal y construye la notificación con su PendingIntent |
| `MyFirebaseMessagingService.kt` | Servicio que recibe los mensajes de FCM y registra el token |
| `NotificacionScreen.kt` | Pantalla principal, permisos, token y pantalla de destino |
| `MainActivity.kt` | Crea el canal y procesa los extras o el Deep Link del Intent |

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- NotificationManager, NotificationChannel y PendingIntent
- Firebase Cloud Messaging (firebase-bom 33.7.0)
- Plugin Google Services 4.4.2

## Configuración de Firebase

El archivo `google-services.json` no está incluido en el repositorio por seguridad. Para ejecutar el proyecto:

1. Entrar a https://console.firebase.google.com y crear un proyecto.
2. Registrar una app Android con el nombre de paquete `com.example.practica15`.
3. Descargar `google-services.json` y copiarlo en la carpeta `app/`.
4. Abrir el proyecto en Android Studio y ejecutar Sync Now.

## Cómo ejecutar

Usar un dispositivo físico o un emulador con imagen Google Play. En Android 13 o superior, aceptar el permiso de notificaciones al abrir la app.

## Cómo probar

**Notificación local**

1. Tocar el botón "Disparar notificación local".
2. Abrir la barra de notificaciones y tocar la alerta.
3. La app abre la pantalla de destino.

**Notificación push**

1. Copiar el token mostrado en pantalla (también aparece en Logcat con el filtro `FCM_TOKEN`).
2. En Firebase Console ir a Messaging, crear una campaña y elegir "Send test message".
3. Pegar el token y enviar.
4. Para probar el Data Payload, agregar en "Datos personalizados" las claves `destino` (valor `detalle`) y `mensaje` (cualquier texto).
5. Tocar la notificación recibida: la app abre la pantalla de destino con el mensaje del payload.

