package com.openclassrooms.hexagonal.games.screen.ad

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme


/**
 * The AddScreen  provides a Screen to create a new post.
 * It includes text fields for the post title and description,
 * an image picker, and buttons to save or go back.
 *
 * @param modifier Modifier for customizing the composable layout.
 * @param viewModel ViewModel instance for managing the form state and actions.
 * @param onBackClick Lambda triggered when the back button is clicked.
 * @param onSaveClick Lambda triggered when the save button is clicked.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState()
    var wasConnected by remember { mutableStateOf(isConnected) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_fragment_label))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        val post by viewModel.post.collectAsStateWithLifecycle()
        val error by viewModel.error.collectAsStateWithLifecycle()
        val noImageSelected = stringResource(R.string.invalid_photo)
        CreatePost(
            modifier = Modifier.padding(contentPadding),
            error = error,
            title = post.title,
            onTitleChanged = { viewModel.onAction(FormEvent.TitleChanged(it)) },
            description = post.description ?: "",
            onDescriptionChanged = { viewModel.onAction(FormEvent.DescriptionChanged(it)) },
            onSaveClicked = { imageUri ->
                if (imageUri != null) {
                    viewModel.onAction(FormEvent.ImageChanges(imageUri))
                    onSaveClick()
                } else {
                    Toast.makeText(context, noImageSelected, Toast.LENGTH_SHORT).show()
                    onSaveClick()
                }
            }
        )
        if (!isConnected && wasConnected) {
            Toast.makeText(context, stringResource(R.string.no_network), Toast.LENGTH_SHORT).show()
        }
        wasConnected = isConnected
    }
}

@Composable
private fun CreatePost(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Uri?) -> Unit,
    error: FormError?
) {
    val scrollState = rememberScrollState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = title,
                isError = error is FormError.TitleError,
                onValueChange = { onTitleChanged(it) },
                label = { Text(stringResource(id = R.string.hint_title)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            if (error is FormError.TitleError) {
                Text(
                    text = stringResource(id = error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = description,
                onValueChange = { onDescriptionChanged(it) },
                label = { Text(stringResource(id = R.string.hint_description)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            )
        } else {
            Text(
                text = stringResource(id = R.string.no_image),
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Button to Pick Image
        Button(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.select_a_photo))
        }

        Button(
            enabled = error == null,
            onClick = { onSaveClicked(selectedImageUri) }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.action_save)
            )
        }
    }

}


@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostPreview() {
    HexagonalGamesTheme {
        CreatePost(
            title = "test",
            onTitleChanged = { },
            description = "description",
            onDescriptionChanged = { },
            onSaveClicked = { },
            error = null
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostErrorPreview() {
    HexagonalGamesTheme {
        CreatePost(
            title = "test",
            onTitleChanged = { },
            description = "description",
            onDescriptionChanged = { },
            onSaveClicked = { },
            error = FormError.TitleError
        )
    }
}