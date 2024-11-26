package com.openclassrooms.hexagonal.games.screen.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.utils.ConnectivityUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to display posts in the DetailScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val firebaseService: FirebaseService,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isUserConnected = MutableStateFlow(false)
    val isUserConnected: StateFlow<Boolean> = _isUserConnected

    init {

        viewModelScope.launch {
            ConnectivityUtils.observeNetworkState(appContext).collect { isConnected ->
                _isConnected.value = isConnected
            }
        }
        viewModelScope.launch {
            firebaseService.addAuthStateListener { user ->
                _isUserConnected.value = user != null
            }
        }
    }


    fun getPostById(postId: String): Flow<Post?> {
        return postRepository.posts.map { posts -> posts.find { it.id == postId } }
    }


}