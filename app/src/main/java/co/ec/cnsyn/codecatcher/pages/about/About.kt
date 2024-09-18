package co.ec.cnsyn.codecatcher.pages.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.LocalSettings
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.AutoText
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.timeString
import co.ec.cnsyn.codecatcher.sms.SmsService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun About() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SkewSquare(
            cut = SkewSquareCut.BottomEnd,
            fill = MaterialTheme.colorScheme.primaryContainer,
            skew = 30,
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "code catcher hook logo",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier.fillMaxWidth(.6F)
                )

                AutoText(
                    text = stringResource(R.string.app_name),
                    fontSize = 15..25,
                    key = "about-app-name",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                )
                Text(
                    text = stringResource(R.string.about_developed_with),
                    style = MaterialTheme.typography.bodyMedium
                )
                var heartBeat by remember { mutableStateOf(0L) }
                val settings = LocalSettings.current
                LaunchedEffect(Unit) {
                    while (true) {
                        //wait for delay and re run
                        heartBeat = settings.getInt("service-heartbeat", 0).toLong()
                        delay(if(heartBeat == 0L) 1000L else SmsService.HEART_BEAT_DELAY * 1000L)
                    }
                }
                if (heartBeat != 0L) {
                    Text(
                        text = stringResource(R.string.about_heartbeat) +
                                " " + heartBeat.dateString() + " " + heartBeat.timeString(),
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall

                    )
                }
            }
        }
        val context = LocalContext.current
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.about_help_to_translate),
                style = MaterialTheme.typography.bodySmall
            )
            val title = stringResource(R.string.about_translate_mail_title)
            OutlinedButton(onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("code-catcher-translate@proxiedmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                context.startActivity(intent)

            }) {
                Text(
                    text = stringResource(R.string.about_translate_contact),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun AboutPreview() {
    CodeCatcherPreview {
        About()
    }
}