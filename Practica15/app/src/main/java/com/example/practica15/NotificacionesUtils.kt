package com.example.practica15

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

const val CANAL_ID = "canal_practica15"

fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            CANAL_ID,
            "Notificaciones Práctica 15",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Alertas locales y push de la Práctica 15" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }
}

fun mostrarNotificacion(
    context: Context,
    titulo: String,
    mensaje: String,
    destino: String = "detalle"
) {
    // En Android 13+ sin permiso no se puede publicar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) return

    val id = System.currentTimeMillis().toInt()

    // Al tocar la notificación abre MainActivity y le manda el destino
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("destino", destino)
        putExtra("mensaje", mensaje)
    }
    val pendingIntent = PendingIntent.getActivity(
        context, id, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificacion = NotificationCompat.Builder(context, CANAL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(titulo)
        .setContentText(mensaje)
        .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(id, notificacion)
}