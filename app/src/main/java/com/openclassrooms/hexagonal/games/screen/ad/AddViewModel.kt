package com.openclassrooms.hexagonal.games.screen.ad

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.plugins.RxJavaPlugins.onError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
//    @ApplicationContext private val appContext: Context,
    private val postRepository: PostRepository,
    private val firebaseService: FirebaseService,


    ) : ViewModel() {

    /**
     * Internal mutable state flow representing the current post being edited.
     */
    private var _post = MutableStateFlow(
        Post(
            id = System.currentTimeMillis().toString(),
            title = "",
            description = "",
            photoUrl = null,
            timestamp = System.currentTimeMillis(),
            author = null,
        )
    )
    private val _isPostError = MutableStateFlow(false)
    val isPostError: StateFlow<Boolean> = _isPostError


//    private val _isConnected = MutableStateFlow(false)
//    val isConnected: StateFlow<Boolean> = _isConnected

//    init {
//        viewModelScope.launch {
//            NetworkUtils.observeNetworkState(appContext).collect { isConnected ->
//                _isConnected.value = isConnected
//            }
//        }
//    }

    /**
     * Public state flow representing the current post being edited.
     * This is immutable for consumers.
     */
    val post: StateFlow<Post>
        get() = _post

    /**
     * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
     */
    val error = post.map {
        verifyPost()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    /**
     * Handles form events like title and description changes.
     *
     * @param formEvent The form event to be processed.
     */
    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.DescriptionChanged -> {
                _post.value = _post.value.copy(
                    description = formEvent.description
                )
            }

            is FormEvent.TitleChanged -> {
                _post.value = _post.value.copy(
                    title = formEvent.title
                )
            }

            is FormEvent.ImageChanges -> {
                firebaseService.uploadImageToFirebase(
                    uri = formEvent.image,
                    onSuccess = { imageUrl ->
                        _post.value = _post.value.copy(
                            photoUrl = imageUrl
                        )
                        addPost()
                    },
                    onError = { exception ->
                        exception.printStackTrace()
                    }
                )
            }
        }
    }

    /**
     * Attempts to add the current post to the repository after setting the author.
     *
     * TODO: Implement logic to retrieve the current user.
     */
    fun addPost() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError(Exception("Utilisateur non authentifié"))
            return
        }
        val completeName = user?.displayName
        val listNames = completeName?.split(" ") ?: listOf()
        val firstName = listNames.firstOrNull() ?: "Inconnu"
        val lastName = listNames.getOrNull(1) ?: "Inconnu"
        val author = User(
            firstname = firstName,
            id = user.uid,
            lastname = lastName
        )
        viewModelScope.launch {
            val result = postRepository.addPost(
                _post.value.copy(
                    author = author
                )
            )
            result.onSuccess {
                _isPostError.value = false
            }.onFailure { exception ->
                _isPostError.value = true
            }
//            postRepository.addPost(
//                _post.value.copy(
//                    author = author
//                )
//            )
        }
    }

    /**
     * Verifies mandatory fields of the post
     * and returns a corresponding FormError if so.
     *
     * @return A FormError.TitleError if title is empty, null otherwise.
     */

    fun verifyPost(): FormError? {
        val currentPost = _post.value
        return when {
            currentPost.title.isEmpty() -> FormError.TitleError
            currentPost.description?.isEmpty() == true -> FormError.DescriptionError
            currentPost.photoUrl.isNullOrEmpty() -> FormError.ImageError
            else -> null
        }
    }
}




