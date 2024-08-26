package co.ec.cnsyn.codecatcher.pages.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.composables.SkewSquare

@Composable
fun Help(
    helpType: String = "",
    helpModel: HelpViewModel = viewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {
        SkewSquare(
            skew = 30,
            fill = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .statusBarsPadding()
            ) {

                Text(text = helpType ?: "no-help")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "help content")
            when (helpType) {
                "service-notification" -> HelpServiceNotification(helpModel)
            }
        }

    }
}

@Composable
fun HelpServiceNotification(helpModel: HelpViewModel) {
    Column {
        Button(onClick = {
            helpModel.openChannelSettings()
        }) {
            Text(text = "Open Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpPreview() {
    CodeCatcherPreview {
        Help()
    }
}