package com.openclassrooms.hexagonal.games.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFABClick: () -> Unit,
    postId: String?,

    ) {
    if (postId == null) {
        Text(text = "Erreur : ID du post manquant")
        return
    }

    val post =
        viewModel.getPostById(postId).collectAsStateWithLifecycle(initialValue = null).value
    if (post != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text( post.title)
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
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onFABClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.description_button_add)
                    )
                }
            }

        ) {


                contentPadding ->

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(contentPadding),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.by,
                        post.author?.firstname ?: "",
                        post.author?.lastname ?: ""
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge
                )
                if (post.description.isNullOrEmpty() == false) {
                    Text(
                        text = post.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (post.photoUrl.isNullOrEmpty() == false) {
                    AsyncImage(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .aspectRatio(ratio = 16 / 9f),
                        model = post.photoUrl,
                        imageLoader = LocalContext.current.imageLoader.newBuilder()
                            .logger(DebugLogger())
                            .build(),
                        placeholder = ColorPainter(Color.DarkGray),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                    )
                }

            }
        }

    } else {
        Text(text = "Loading")
    }
}