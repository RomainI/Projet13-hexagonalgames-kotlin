package com.openclassrooms.hexagonal.games.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Méthode appelée lorsqu'un message est reçu
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Log pour afficher les données du message
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Log pour afficher les informations de la notification (si présentes)
        remoteMessage.notification?.let {
            Log.d(TAG, "Message notification title: ${it.title}")
            Log.d(TAG, "Message notification body: ${it.body}")
        }
        // Récupérer les données de la notification
        val title = remoteMessage.notification?.title ?: "Notification"
        val message = remoteMessage.notification?.body ?: "Vous avez un nouveau message."

        // Afficher la notification
        showNotification(title, message)
    }

    // Méthode appelée lorsqu'un nouveau token est généré
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token: $token")
        // Vous pouvez envoyer ce token à votre serveur si nécessaire

    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel"
        val notificationId = 1

        // Gestion du canal de notification (nécessaire pour Android 8.0+)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifications générales",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intention pour ouvrir l'application au clic sur la notification
        val intent = Intent(this, MainActivity::class.java) // Remplacez par l'activité à ouvrir
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construire la notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications)
            .setAutoCancel(true) // La notification disparaît après le clic
            .setContentIntent(pendingIntent) // Action lorsqu'on clique sur la notification
            .build()

        // Afficher la notification
        notificationManager.notify(notificationId, notification)
    }


}