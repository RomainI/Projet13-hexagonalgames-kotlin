package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.User
import javax.inject.Inject

class FirebaseService @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun addAuthStateListener(onAuthStateChanged: (FirebaseUser?) -> Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            onAuthStateChanged(firebaseAuth.currentUser)
        }
    }
    fun uploadImageToFirebase(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val fileName = "images/${System.currentTimeMillis()}.jpg"
        val fileRef = storageReference.child(fileName)

        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString()) // Retourne l'URL de l'image téléchargée
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
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