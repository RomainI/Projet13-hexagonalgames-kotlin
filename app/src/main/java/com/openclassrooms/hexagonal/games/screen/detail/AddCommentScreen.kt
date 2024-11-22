package com.openclassrooms.hexagonal.games.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.screen.ad.FormError
import com.openclassrooms.hexagonal.games.screen.ad.FormEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(onBackClick: () -> Unit,
                     postId: String?,
                     viewModel: AddCommentViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_a_comment))
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
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth(),
                value = viewModel.comment.value,
                onValueChange = { viewModel.updateComment(it) },
                label = { Text(stringResource(id = R.string.comment)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )


            Button(
                enabled = viewModel.isCommentFilled(),
                onClick = { onBackClick()
                    if (postId != null) {
                        viewModel.addComment(postId)
                    }
                }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.action_save)
                )
            }
        }
    }
}


