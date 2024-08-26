package co.ec.cnsyn.codecatcher.pages.help

import android.text.Html
import android.widget.TextView
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.helpers.translate

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
                    .padding(vertical = 16.dp)
            ) {

                Text(text = translate("help_${helpType}_title"),
                    style = MaterialTheme.typography.titleLarge)
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
                "service_notification" -> HelpServiceNotification(helpModel)
            }
        }

    }
}

@Composable
fun HelpServiceNotification(helpModel: HelpViewModel) {
    Column(modifier = Modifier.padding(8.dp),) {
        var text= stringResource(id = R.string.help_service_notification_content)
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context -> TextView(context) },
            update = { it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT) }
        )
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