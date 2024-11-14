package com.openclassrooms.hexagonal.games.screen.management

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.openclassrooms.hexagonal.games.R
import dagger.hilt.android.lifecycle.HiltViewModel
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
                //TODO POPUP
                CoroutineScope(Dispatchers.IO).launch {
                    async {
                        viewModel.deleteUser()
                    }.await()
                }
                onBackClick()
            },
            onSignOutButtonClicked = {
                viewModel.logOut()
                onBackClick()
            },
        )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MyAccount(
    modifier: Modifier = Modifier,
    onDeleteButtonClicked: () -> Unit,
    onSignOutButtonClicked: () -> Unit,
) {
    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        null
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (notificationsPermissionState?.status?.isGranted == false) {
                        notificationsPermissionState.launchPermissionRequest()
                    }
                }

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

