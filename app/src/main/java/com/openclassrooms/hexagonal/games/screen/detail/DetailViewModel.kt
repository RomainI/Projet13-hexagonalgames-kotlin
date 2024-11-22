package com.openclassrooms.hexagonal.games.screen.detail

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    fun getPostById(postId: String): Flow<Post?> {
        return postRepository.posts.map { posts -> posts.find { it.id == postId } }
    }
}