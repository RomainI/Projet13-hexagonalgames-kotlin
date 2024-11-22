package com.openclassrooms.hexagonal.games.screen.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import androidx.compose.runtime.State
import com.openclassrooms.hexagonal.games.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(private val postRepository: PostRepository, private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val _comment = mutableStateOf("")
    val comment: State<String> = _comment

    fun isCommentFilled(): Boolean {
        return _comment.value.isNotEmpty()
    }

    fun updateComment(newComment: String) {
        _comment.value = newComment
    }
    val name = firebaseRepository.getCurrentUser()?.displayName
    fun addComment(postId : String) {
        if (name != null) {
            postRepository.addComment(postId, _comment.value, name)
        }
    }
}