package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class PostFirebaseApi :PostApi {
    private val users = mutableListOf(
        User("1", "Gerry", "Ariella",
            /**email = null, name = null*/ /**email = null, name = null*/),
        User("2", "Brenton", "Capri",
            /**email = null, name = null*/ /**email = null, name = null*/),
        User("3", "Wally", "Claud",
            /**email = null, name = null*/ /**email = null, name = null*/)
    )
    val db = FirebaseFirestore.getInstance()

    private val posts = MutableStateFlow(
        mutableListOf(
            Post(
                "5",
                "The Secret of the Flowers",
                "Improve your goldfish's physical fitness by getting him a bicycle.",
                null,
                1629858873, // 25/08/2021
                users[0]
            ),
            Post(
                "4",
                "The Door's Game",
                null,
                "https://picsum.photos/id/85/1080/",
                1451638679, // 01/01/2016
                users[2]
            ),
            Post(
                "1",
                "Laughing History",
                "He learned the important lesson that a picnic at the beach on a windy day is a bad idea.",
                "",
                1361696994, // 24/02/2013
                users[0]
            ),
            Post(
                "3",
                "Woman of Years",
                "After fighting off the alligator, Brian still had to face the anaconda.",
                null,
                1346601558, // 02/09/2012
                users[0]
            ),
            Post(
                "2",
                "The Invisible Window",
                null,
                "https://picsum.photos/id/40/1080/",
                1210645031, // 13/05/2008
                users[1]
            ),
        )
    )



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

            Post(
                id = id,
                title = title,
                description = description,
                photoUrl = photoUrl,
                timestamp = timestamp,
                author = author
            )
        } catch (e: Exception) {
            Log.e("PostFirebaseApi", "Erreur lors du mapping : ${e.message}")
            null
        }
    }

}