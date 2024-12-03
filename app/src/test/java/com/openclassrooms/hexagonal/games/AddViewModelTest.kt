import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.service.FirebaseService
import com.openclassrooms.hexagonal.games.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.screen.ad.FormError
import com.openclassrooms.hexagonal.games.screen.ad.FormEvent

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest {

    private lateinit var viewModel: AddViewModel
    private val postRepository: PostRepository = mockk(relaxed = true)
    private val firebaseService: FirebaseService = mockk(relaxed = true)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(StandardTestDispatcher())

        // Mock FirebaseAuth.getInstance()
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth

        // Mock current user
        val mockUser = mockk<FirebaseUser> {
            every { displayName } returns "Test User"
            every { uid } returns "12345"
        }
        every { mockAuth.currentUser } returns mockUser

        // Mock FirebaseService and PostRepository
        coEvery { firebaseService.uploadImageToFirebase(any(), any(), any()) } answers {
            secondArg<(String) -> Unit>().invoke("mockImageUrl")
        }
        coEvery { postRepository.addPost(any()) } returns Result.success(Unit)

        viewModel = AddViewModel(postRepository, firebaseService)
    }

    @Test
    fun `verify post validation passes`() = runTest {
        val title = "Valid Title"
        val description = "Valid Description"
        val uri = mockk<Uri>()

        viewModel.onAction(FormEvent.TitleChanged(title))
        viewModel.onAction(FormEvent.DescriptionChanged(description))
        viewModel.onAction(FormEvent.ImageChanges(uri))

        assert(viewModel.verifyPost() == null)
    }

    @Test
    fun `verify post validation fails with missing fields`() = runTest {
        viewModel.onAction(FormEvent.TitleChanged(""))
        viewModel.onAction(FormEvent.DescriptionChanged("Valid Description"))

        assert(viewModel.verifyPost() is FormError.TitleError)
    }



}