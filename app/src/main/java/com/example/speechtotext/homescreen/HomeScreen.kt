package com.example.speechtotext.homescreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    val state = viewModel.observeWithoutActions()
    val onHandleEvent: (HomeViewModel.UiEvent) -> Unit = viewModel::handleEvent
    val speechToTextResultOne = remember { mutableStateOf("Your result will be displayed here") }
    val speechToTextResultTwo = remember { mutableStateOf("Your result will be displayed here") }
    val speechToTextResultThree = remember { mutableStateOf("Your result will be displayed here") }
    val customDialogMicrophoneContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            onHandleEvent(HomeViewModel.UiEvent.DisplaySpeechToTextDialog)
        } else {
            onHandleEvent(HomeViewModel.UiEvent.DisplayPermissionDialog)
        }
    }
    val noDialogMicrophoneContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onBufferReceived(p0: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(p0: Bundle?) {}
                override fun onEvent(p0: Int, p1: Bundle?) {}
                override fun onRmsChanged(p0: Float) {}
                override fun onError(p0: Int) {
                    speechToTextResultThree.value = "Error..."
                }

                override fun onResults(p0: Bundle?) {
                    val result = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    speechToTextResultThree.value = result?.firstOrNull() ?: "Error..."
                }
            })
            speechRecognizer.startListening(speechRecognizerIntent)
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
            onClick = { customDialogMicrophoneContract.launch(Manifest.permission.RECORD_AUDIO) },
        )
        Tile(
            title = "No Dialog",
            subtitle = speechToTextResultThree.value,
            onClick = { noDialogMicrophoneContract.launch(Manifest.permission.RECORD_AUDIO) },
        )
    }

    when {
        state.shouldDisplaySpeechToTextDialog -> {
            SpeechToTextDialog(
                speechRecognizer = speechRecognizer,
                speechRecognizerIntent = speechRecognizerIntent,
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

    DisposableEffect(Unit) {
        onDispose { speechRecognizer.destroy() }
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
