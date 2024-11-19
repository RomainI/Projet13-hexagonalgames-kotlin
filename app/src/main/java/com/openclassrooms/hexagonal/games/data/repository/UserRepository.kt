import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.openclassrooms.hexagonal.games.domain.model.User

class UserRepository {

    companion object {
        private const val COLLECTION_NAME = "users"
        private const val USERNAME_FIELD = "username"
        private const val IS_MENTOR_FIELD = "isMentor"
    }

    // Référence à la collection "users"
    private fun getUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    // Créer un utilisateur dans Firestore
    fun createUser() {
        val user = getCurrentUser()
       /** user?.let {
            val urlPicture = user.photoUrl?.toString()
            val username = user.displayName
            val uid = user.uid

            val userToCreate = User(uid, username, urlPicture)

            val userData = getUserData()
            userData?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.contains(IS_MENTOR_FIELD)) {
                    userToCreate.isMentor = documentSnapshot.getBoolean(IS_MENTOR_FIELD) ?: false
                }
                getUsersCollection().document(uid).set(userToCreate)
            }
        }*/
    }

    // Récupérer les données d'un utilisateur
    fun getUserData(): Task<DocumentSnapshot>? {
        val uid = getCurrentUserUID()
        return uid?.let {
            getUsersCollection().document(it).get()
        }
    }

    // Mettre à jour le nom d'utilisateur
    fun updateUsername(username: String): Task<Void>? {
        val uid = getCurrentUserUID()
        return uid?.let {
            getUsersCollection().document(it).update(USERNAME_FIELD, username)
        }
    }

    // Mettre à jour le statut "isMentor"
    fun updateIsMentor(isMentor: Boolean) {
        val uid = getCurrentUserUID()
        uid?.let {
            getUsersCollection().document(it).update(IS_MENTOR_FIELD, isMentor)
        }
    }

    // Supprimer l'utilisateur de Firestore
    fun deleteUserFromFirestore() {
        val uid = getCurrentUserUID()
        uid?.let {
            getUsersCollection().document(it).delete()
        }
    }

    // Récupérer l'utilisateur Firebase courant
    private fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    // Récupérer l'UID de l'utilisateur courant
    private fun getCurrentUserUID() = getCurrentUser()?.uid
}