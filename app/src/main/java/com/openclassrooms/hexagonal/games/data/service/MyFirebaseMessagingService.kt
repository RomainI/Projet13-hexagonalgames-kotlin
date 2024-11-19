package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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


}