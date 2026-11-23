package com.example.practica15

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Se llama cuando Firebase genera o renueva el token del dispositivo
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", token)
    }

    // Mensaje recibido con la app en primer plano (o mensajes solo de datos)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val titulo = remoteMessage.notification?.title ?: data["titulo"] ?: "Notificación push"
        val mensaje = remoteMessage.notification?.body ?: data["mensaje"] ?: "Sin contenido"
        val destino = data["destino"] ?: "detalle"   // Data Payload

        Log.d("FCM_MSG", "titulo=$titulo mensaje=$mensaje destino=$destino")
        mostrarNotificacion(this, titulo, mensaje, destino)
    }
}