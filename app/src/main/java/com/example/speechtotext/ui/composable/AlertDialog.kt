package com.example.speechtotext.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.speechtotext.R
import com.example.speechtotext.ui.theme.Theme

@Composable
fun AlertDialog(
    descriptionBody: String,
    positiveButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "",
    negativeButtonText: String = "",
    isCancellable: Boolean = true,
    @DrawableRes imageResource: Int? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = isCancellable,
            dismissOnBackPress = isCancellable
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .clip(shape = RoundedCornerShape(16f))
                .background(color = colorResource(R.color.white))
                .padding(top = 32.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        ) {
            if (imageResource != null) {
                Icon(
                    painter = painterResource(imageResource),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(height = 16.dp))
            }

            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(height = 32.dp))
            }

            Text(
                text = descriptionBody,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            if (positiveButtonText.isNotEmpty()) {
                Button(
                    onClick = onConfirmation,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = positiveButtonText)
                }
            }

            if (negativeButtonText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(height = 10.dp))

                Button(
                    onClick = onDecline,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = negativeButtonText)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogPreview() {
    Theme {
        AlertDialog(
            title = "Alert Dialog",
            descriptionBody = "This is the body of the alert dialog",
            positiveButtonText = "Confirm",
            onDismissRequest = {},
            onConfirmation = {},
            onDecline = {},
        )
    }
}
