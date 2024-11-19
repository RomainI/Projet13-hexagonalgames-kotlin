package com.openclassrooms.hexagonal.games.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
class SettingsViewModel : ViewModel() {
  /**
   * Enables notifications for the application.
   * TODO: Implement the logic to enable notifications, likely involving interactions with a notification manager.
   */
  fun enableNotifications(topic: String = "all_users", onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      FirebaseMessaging.getInstance().subscribeToTopic(topic)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onComplete(true) // Successfully subscribed
          } else {
            onComplete(false) // Error subscribing
          }
        }
    }

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val token = task.result
        Log.d("FCM Token", "Token : $token")
      } else {
        Log.e("FCM Token", "Erreur pour obtenir le token", task.exception)
      }
    }
    FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val installationId = task.result
        // Utilisez cet ID pour vos besoins
        Log.d("InstallationID", "Installation ID: $installationId")
      } else {
        Log.e("InstallationID", "Échec lors de l'obtention de l'Installation ID", task.exception)
      }
    }

  }
  
  /**
   * Disables notifications for the application.
   * TODO: Implement the logic to disable notifications, likely involving interactions with a notification manager.
   */
  fun disableNotifications(topic: String = "all_users", onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onComplete(true) // Successfully unsubscribed
          } else {
            onComplete(false) // Error unsubscribing
          }
        }
    }
  }
  
}
