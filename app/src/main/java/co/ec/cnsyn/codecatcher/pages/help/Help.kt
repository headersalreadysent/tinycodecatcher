package co.ec.cnsyn.codecatcher.pages.help

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.helpers.htmlToAnnotatedString
import co.ec.cnsyn.codecatcher.helpers.translate
import java.io.InputStream
import java.util.Locale

@Composable
fun Help(
    helpType: String = "service_notification",
    helpModel: HelpViewModel = viewModel()
) {

    Column(modifier = Modifier
        .fillMaxSize()
        .semantics {
            contentDescription = "help show page"
        }) {
        SkewSquare(
            skew = 30,
            fill = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translate("help_${helpType}_title"),
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    Icons.AutoMirrored.Filled.HelpCenter, contentDescription = "help center",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 75.dp)
                .weight(1F)
                .verticalScroll(rememberScrollState())
        ) {
            when (helpType) {
                "service_notification" -> HelpServiceNotification(helpModel)
                "permission" -> PermissionHelp(helpModel)
            }
        }

    }
}

@Composable
fun HelpServiceNotification(helpModel: HelpViewModel) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {

        SkewSquare(
            cut = SkewSquareCut.TopStart,
            skew = 30,
            fill = MaterialTheme.colorScheme.secondaryContainer
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = htmlToAnnotatedString(stringResource(R.string.help_service_notification_content)),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Justify
                    )
                )
                HelpImage("service_notification")
                Button(
                    modifier = Modifier.fillMaxWidth(),

                    onClick = {
                        helpModel.openChannelSettings()
                    }) {
                    Text(text = translate("help_open_settings"))
                }
            }

        }
    }


}

@Composable
fun PermissionHelp(helpModel: HelpViewModel) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {

        SkewSquare(
            cut = SkewSquareCut.TopStart,
            skew = 30,
            fill = MaterialTheme.colorScheme.secondaryContainer
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = htmlToAnnotatedString(stringResource(R.string.help_permission_content)),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Justify
                    )
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),

                    onClick = {
                        helpModel.openPermissionSettings()
                    }) {
                    Text(text = translate("help_open_settings"))
                }

            }

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

@Composable
fun HelpImage(fileName: String) {
    val imageBitmap: ImageBitmap? = loadBitmapFromAssets(LocalContext.current, fileName)
    imageBitmap?.let {
        Image(
            bitmap = it, contentDescription = "help image $fileName",
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)),
        )
    }
}

/**
 * load help assets
 */
fun loadBitmapFromAssets(context: Context, fileName: String): ImageBitmap? {
    val assetManager = context.assets
    return try {
        //first look for language tag folder
        val tagFolder = Locale.getDefault().toLanguageTag()
        val inputStream: InputStream = assetManager.open("help/${tagFolder}/${fileName}.png")
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        try {
            //look normal help folder
            val inputStream: InputStream = assetManager.open("help/${fileName}.png")
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap.asImageBitmap()
        } catch (e2: Exception) {
            //sorry
            null
        }
    }
}