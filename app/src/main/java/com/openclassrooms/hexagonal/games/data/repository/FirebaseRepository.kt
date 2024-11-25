package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import javax.inject.Inject

/**
 * Repository class to manage user-related Firebase operations.
 *
 * @param firebaseService The Firebase service used to perform user-related operations.
 */

class FirebaseRepository @Inject constructor (private val firebaseService: FirebaseService) {

    fun logOut(){
        firebaseService.signOut()
    }

    fun getCurrentUser(): FirebaseUser?{
        return firebaseService.getCurrentUser()
    }

    suspend fun deleteUser() : Boolean{
        return firebaseService.deleteUser()
    }
}