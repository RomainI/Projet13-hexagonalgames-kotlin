package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.openclassrooms.hexagonal.games.domain.model.User
import javax.inject.Inject

class FirebaseService @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            // Gestion des erreurs
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun saveUser(user: User): Boolean {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val document: DocumentSnapshot = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }


    suspend fun deleteUser(): Boolean {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            try {
                firestore.collection("users").document(currentUser.uid).delete().await()
                currentUser.delete().await()
                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
}