package com.example.speechtotext.homescreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.observeWithoutActions()
    val onHandleEvent: (HomeViewModel.UiEvent) -> Unit = viewModel::handleEvent
    val speechToTextResultOne = remember { mutableStateOf("Your result will be displayed here") }
    val speechToTextResultTwo = remember { mutableStateOf("Your result will be displayed here") }
    val microphoneContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            onHandleEvent(HomeViewModel.UiEvent.DisplaySpeechToTextDialog)
        } else {
            onHandleEvent(HomeViewModel.UiEvent.DisplayPermissionDialog)
        }
    }
    val defaultSpeechToTextLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            speechToTextResultOne.value = result?.get(0) ?: "Error..."
        } else {
            speechToTextResultOne.value = "Error..."
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),

        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Tile(
            title = "Default Google Dialog",
            subtitle = speechToTextResultOne.value,
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...")
                defaultSpeechToTextLauncher.launch(intent)
            },
        )
        Tile(
            title = "Custom Dialog",
            subtitle = speechToTextResultTwo.value,
            onClick = { microphoneContract.launch(Manifest.permission.RECORD_AUDIO) },
        )
    }

    when {
        state.shouldDisplaySpeechToTextDialog -> {
            SpeechToTextDialog(
                onDismissRequest = { onHandleEvent(HomeViewModel.UiEvent.DismissSpeechToTextDialog) },
                onSpeechToTextResult = { speechToTextResultTwo.value = it }
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
