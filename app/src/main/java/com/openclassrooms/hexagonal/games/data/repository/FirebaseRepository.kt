package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import javax.inject.Inject

class FirebaseRepository @Inject constructor (private val firebaseService: FirebaseService) {

    suspend fun getFirebaseUserWithSignInWithEmail(email : String, password : String): FirebaseUser?{
        return firebaseService.signInWithEmail(email,password)
    }

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