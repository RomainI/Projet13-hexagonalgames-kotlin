package com.openclassrooms.hexagonal.games.screen.homefeed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.utils.ConnectivityUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing data and events related to the Homefeed.
 * This ViewModel retrieves posts from the PostRepository and exposes them as a Flow<List<Post>>,
 * allowing UI components to observe and react to changes in the posts data.
 */
@HiltViewModel
class HomefeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val firebaseService: FirebaseService,
    @ApplicationContext private val appContext: Context
) :
    ViewModel() {

    private val _posts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isUserConnected = MutableStateFlow(false)
    val isUserConnected: StateFlow<Boolean> = _isUserConnected

    /**
     * Returns a Flow observable containing the list of posts fetched from the repository.
     *
     * @return A Flow<List<Post>> object that can be observed for changes.
     */
    val posts: StateFlow<List<Post>>
        get() = _posts

    init {
        viewModelScope.launch {
            postRepository.posts.collect { posts ->
                _posts.value = posts
            }
        }
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

}
