package com.example.speechtotext.homescreen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speechtotext.BuildConfig
import com.example.speechtotext.base.observeWithoutActions
import com.example.speechtotext.ui.composable.AlertDialog
import com.example.speechtotext.ui.composable.SpeechToTextDialog
import com.example.speechtotext.ui.composable.Tile
import com.example.speechtotext.ui.theme.Theme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.observeWithoutActions()
    val onHandleEvent: (HomeViewModel.UiEvent) -> Unit = viewModel::handleEvent
    val microphoneContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            onHandleEvent(HomeViewModel.UiEvent.DisplaySpeechToTextDialog)
        } else {
            onHandleEvent(HomeViewModel.UiEvent.DisplayPermissionDialog)
        }
    }
    var speechToTextResult by remember { mutableStateOf("Your result will be displayed here") }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),

        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Tile(
            title = "Default Google Dialog",
            subtitle = "Not Implemented",
            onClick = {},
        )
        Tile(
            title = "Custom Dialog",
            subtitle = speechToTextResult,
            onClick = { microphoneContract.launch(Manifest.permission.RECORD_AUDIO) },
        )
        Tile(
            title = "No Dialog",
            subtitle = "Not Implemented",
            onClick = {},
        )
    }

    when {
        state.shouldDisplaySpeechToTextDialog -> {
            SpeechToTextDialog(
                onDismissRequest = { onHandleEvent(HomeViewModel.UiEvent.DismissSpeechToTextDialog) },
                onSpeechToTextResult = { speechToTextResult = it }
            )
        }
        state.shouldDisplayPermissionDialog -> {
            MicrophonePermissionAlertDialog(
                onLocationDialogConfirmClick = {
                    onHandleEvent(HomeViewModel.UiEvent.DisplayPermissionDialog)
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
                    context.startActivity(intent)
                },
                onLocationDialogDismissClick = { onHandleEvent(HomeViewModel.UiEvent.DismissPermissionDialog) },
            )
        }
    }
}

@Composable
private fun MicrophonePermissionAlertDialog(
    onLocationDialogConfirmClick: () -> Unit,
    onLocationDialogDismissClick: () -> Unit,
) {
    AlertDialog(
        title = "No Mic Permission",
        descriptionBody = "Please go to setting and enable the microphone permission",
        positiveButtonText = "Go to setting",
        negativeButtonText = "Cancel",
        onConfirmation = onLocationDialogConfirmClick,
        onDismissRequest = onLocationDialogDismissClick,
        onDecline = onLocationDialogDismissClick,
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Theme {
        HomeScreen()
    }
}
