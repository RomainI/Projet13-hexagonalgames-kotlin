package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

/**
 * This class represents a Comment associated with a Post.
 * It holds information about the comment content, the author, and the timestamp.
 */
data class Comment(
    /**
     * The content of the comment.
     */
    val content: String,

    /**
     * First name of the user who made the comment.
     */
    val commentFirstName: String,

    /**
     * Last name of the user who made the comment.
     */
    val commentLastName: String,

    /**
     * Timestamp of the comment in milliseconds since epoch.
     */
    val timestamp: Long
) : Serializable