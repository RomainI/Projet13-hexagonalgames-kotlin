package com.openclassrooms.hexagonal.games.screen.management

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagement(
    modifier: Modifier = Modifier,
    viewModel: AccountManagementViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.my_account))
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


        MyAccount(
            modifier = Modifier.padding(contentPadding),
            onDeleteButtonClicked = {
                showDialog = true
            },
            onSignOutButtonClicked = {
                viewModel.logOut()
                onBackClick()
            },
        )
        if (showDialog) {
            DeleteAlertDialog(
                title = stringResource(R.string.title_alert_delete),
                text = stringResource(R.string.text_alert_delete),
                onDismiss = {
                    showDialog = false
                    onBackClick()
                },
                onConfirm = {
                    CoroutineScope(Dispatchers.IO).launch {
                        async {
                            viewModel.deleteUser()
                        }.await()
                    }
                    showDialog = false
                    onBackClick()
                }
            )
        }
    }
}


@Composable
private fun MyAccount(
    modifier: Modifier,
    onDeleteButtonClicked: () -> Unit,
    onSignOutButtonClicked: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                onSignOutButtonClicked()
            }
        ) {
            Text(text = stringResource(id = R.string.sign_out))
        }
        Button(
            onClick = { onDeleteButtonClicked() }
        ) {
            Text(text = stringResource(id = R.string.delete_account))
        }
    }
}

@Composable
fun DeleteAlertDialog(title: String, text: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirmer")
                onConfirm
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Annuler")
            }
        }
    )

}
