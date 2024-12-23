package com.openclassrooms.hexagonal.games.screen.management

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * This ViewModel manages data and interactions in the AccountManagementScreen.
 * It utilizes dependency injection to retrieve a firtebaserepository instance
 */

@HiltViewModel
class AccountManagementViewModel
@Inject constructor(private val firebaseRepository: FirebaseRepository) :
    ViewModel() {

    fun logOut() {
        firebaseRepository.logOut()
    }

    suspend fun deleteUser() : Boolean {
        return firebaseRepository.deleteUser()
    }

    fun isUserLoggedIn(): Boolean{
        return firebaseRepository.getCurrentUser()!=null
    }

}