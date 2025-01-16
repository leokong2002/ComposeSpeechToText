package com.example.speechtotext.ui.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speechtotext.base.observeWithoutActions

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state = viewModel.observeWithoutActions()
    val onHandleEvent: (HomeViewModel.UiEvent) -> Unit = viewModel::handleEvent
    val microphoneContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            onHandleEvent(HomeViewModel.UiEvent.DisplaySpeechToTextDialog)
        } else {
            onHandleEvent(HomeViewModel.UiEvent.DisplayPermissionDialog)
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
            title = "Title",
            subtitle = state.shouldDisplayPermissionDialog.toString(),
            onClick = { microphoneContract.launch(android.Manifest.permission.RECORD_AUDIO) },
        )
        Tile(
            title = "Title",
            subtitle = "Subtitle",
            onClick = {},
        )
        Tile(
            title = "Title",
            subtitle = "Subtitle",
            onClick = {},
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Theme {
//        HomeScreen()
//    }
//}