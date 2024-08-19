package co.ec.cnsyn.codecatcher.pages.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.LocalSettings
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewBottomSheet
import co.ec.cnsyn.codecatcher.composables.ToggleButton
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    close: () -> Unit = { -> }
) {
    SkewBottomSheet(
        onDismissRequest = {
            close()
        },
        fill = MaterialTheme.colorScheme.secondaryContainer

    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            val settings = LocalSettings.current
            Text(
                text = "Settings",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var copy by remember {
                    mutableStateOf(settings.getBoolean("copyAllCodes", false))
                }
                Text(
                    stringResource(R.string.settings_copy_all_codes_to_clipboard),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Checkbox(checked = copy, onCheckedChange = {
                    copy = !copy
                    settings.putBoolean("copyAllCodes", copy)
                })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var copy by remember {
                    mutableStateOf(settings.getBoolean("dynamicColor", true))
                }
                Text(
                    stringResource(R.string.settings_use_dynamic_template),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Checkbox(checked = copy, onCheckedChange = {
                    copy = !copy
                    settings.putBoolean("dynamicColor", copy)
                })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var darkMode by remember {
                    mutableStateOf(settings.getInt("darkModeSelection", 0))
                }
                Text(
                    stringResource(R.string.settings_use_dark_mode),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                val options = stringArrayResource(R.array.settings_use_dark_options)

                ToggleButton(
                    value = darkMode,
                    values = mapOf(
                        -1 to options[0],
                        0 to options[1],
                        1 to options[2]
                    ),
                    onChange = {
                        darkMode=it as Int
                        settings.putInt("darkModeSelection", darkMode)
                    }
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    CodeCatcherTheme {
        SettingsModal()
    }
}