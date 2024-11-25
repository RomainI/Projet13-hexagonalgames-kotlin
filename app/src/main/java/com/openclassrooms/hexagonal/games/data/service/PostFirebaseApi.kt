package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


/**
 * Class to interact with Firebase Firestore for managing Posts and Comments.
 * Implements PostApi interface
 */

class PostFirebaseApi :PostApi {

    private val db = FirebaseFirestore.getInstance()

    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> = callbackFlow {
        val db = FirebaseFirestore.getInstance()
        val postsCollection = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)



        val subscription = postsCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val posts = snapshot.documents.mapNotNull { document ->
                    try {
                        documentToPost(document)
                    } catch (e: Exception) {
                        Log.e("PostFirebaseApi", "Erreur de conversion : ${e.message}")
                        null
                    }
                }
                trySend(posts).isSuccess
            } else {
                trySend(emptyList()).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun addPost(post: Post) {



        db.collection("posts").add(post)
            .addOnSuccessListener { Log.d("Firestore", "Message ajouté !") }
            .addOnFailureListener { e -> Log.w("Firestore", "Erreur lors de l'ajout", e) }

    }

    override fun addCommentToPost(postId: String, comment: String, name : String) {
        val postsCollection = db.collection("posts")

        postsCollection.whereEqualTo("id", postId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val parts = name.split(" ")
                    val firstName = parts.getOrNull(0) ?: "Inconnu"
                    val lastName = parts.getOrNull(1) ?: "Inconnu"
                    val document = querySnapshot.documents[0]
                    val documentRef = document.reference
                    val commentList = mapOf(
                        "comment" to comment,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "timestamp" to System.currentTimeMillis()
                    )
                    documentRef.update("comments", FieldValue.arrayUnion(commentList))
                        .addOnSuccessListener {
                            Log.d("Firestore", "Commentaire ajouté avec succès !")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erreur lors de l'ajout du commentaire : ${e.message}")
                        }
                } else {
                    Log.e("Firestore", "Aucun document trouvé avec le postId : $postId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erreur lors de la recherche du document : ${e.message}")
            }
    }

    private fun documentToPost(document: com.google.firebase.firestore.DocumentSnapshot): Post? {
        return try {
            val id = document.getString("id") ?: return null
            val title = document.getString("title") ?: return null
            val description = document.getString("description")
            val photoUrl = document.getString("photoUrl")
            val timestamp = document.getLong("timestamp") ?: return null
            val authorMap = document.get("author") as? Map<*, *>

            val author = authorMap?.let {
                User(
                    id = it["id"] as? String ?: return null,
                    firstname = it["firstname"] as? String ?: return null,
                    lastname = it["lastname"] as? String ?: return null
                )
            }

            val comments = (document.get("comments") as? List<*>)?.mapNotNull { commentEntry ->
                (commentEntry as? Map<*, *>)?.let { commentMap ->
                    try {
                        Comment(
                            content = commentMap["comment"] as? String ?: return@mapNotNull null,
                            commentFirstName = commentMap["firstName"] as? String ?: return@mapNotNull null,
                            commentLastName = commentMap["lastName"] as? String ?: return@mapNotNull null,
                            timestamp = (commentMap["timestamp"] as? Long) ?: return@mapNotNull null
                        )
                    } catch (e: Exception) {
                        Log.e("PostFirebaseApi", "Erreur lors du mapping d'un commentaire : ${e.message}")
                        null
                    }
                }
            } ?: emptyList()

            Post(
                id = id,
                title = title,
                description = description,
                photoUrl = photoUrl,
                timestamp = timestamp,
                author = author,
                comments = comments
            )
        } catch (e: Exception) {
            Log.e("PostFirebaseApi", "Erreur lors du mapping : ${e.message}")
            null
        }
    }

}