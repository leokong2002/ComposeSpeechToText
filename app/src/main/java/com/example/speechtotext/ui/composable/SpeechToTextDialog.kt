package com.example.speechtotext.ui.composable

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.speechtotext.R
import com.example.speechtotext.ui.theme.Theme

private const val PulseFractionMultiplier = 0.1f

@Composable
fun SpeechToTextDialog(
    onDismissRequest: () -> Unit,
    onSpeechToTextResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    var amplitude by remember { mutableFloatStateOf(0f) }
    var hasError by remember { mutableStateOf(false) }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onBufferReceived(p0: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(p0: Bundle?) {}
        override fun onEvent(p0: Int, p1: Bundle?) {}
        override fun onRmsChanged(p0: Float) {
            amplitude = if (p0 > 0) p0 * PulseFractionMultiplier else 0f
        }

        override fun onError(p0: Int) {
            hasError = true
        }

        override fun onResults(p0: Bundle?) {
            val result = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            result?.firstOrNull()?.let { searchTerm ->
                onSpeechToTextResult(searchTerm)
                onDismissRequest()
            } ?: run { hasError = true }
        }
    })

    SpeechToTextDialogScreen(
        hasError = hasError,
        amplitude = amplitude,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )

    DisposableEffect(Unit) {
        speechRecognizer.startListening(speechRecognizerIntent)
        onDispose {
            speechRecognizer.destroy()
        }
    }
}

@Composable
private fun SpeechToTextDialogScreen(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isCancellable: Boolean = true,
    hasError: Boolean = false,
    amplitude: Float = 0f,
    pulsatingCircleColour: Color = Color(color = 0x99D9D9D9),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = isCancellable,
            dismissOnBackPress = isCancellable,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .clip(shape = RoundedCornerShape(16f))
                .background(color = colorResource(R.color.white))
                .padding(
                    top = 32.dp,
                    bottom = 24.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
                PulsatingCircle(pulsatingCircleColour = pulsatingCircleColour, pulseFraction = amplitude)
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(shape = CircleShape, color = colorResource(if (!hasError) R.color.green else R.color.red))
                        .align(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        tint = colorResource(R.color.white),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            Text(
                text = if (hasError) "Error..." else "Listening...",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Cancel")
            }
        }
    }
}

@Composable
private fun PulsatingCircle(
    pulsatingCircleColour: Color,
    modifier: Modifier = Modifier,
    pulseFraction: Float = 0f,
) {
    val scale by animateFloatAsState(
        targetValue = pulseFraction,
        animationSpec = tween(durationMillis = 100),
        label = "PulseAnimation"
    )
    Box(modifier = modifier.scale(scale)) {
        Surface(
            color = pulsatingCircleColour,
            shape = CircleShape,
            modifier = Modifier.fillMaxSize(),
            content = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DUSpeechToTextDialogPreview() {
    Theme {
        SpeechToTextDialogScreen(onDismissRequest = {})
    }
}
