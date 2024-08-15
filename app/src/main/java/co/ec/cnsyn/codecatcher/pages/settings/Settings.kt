package co.ec.cnsyn.codecatcher.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.LocalSettings
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewDialog
import co.ec.cnsyn.codecatcher.sms.ActionRunner
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@Composable
fun SettingsModal() {
    Column(modifier = Modifier.fillMaxWidth()) {
        val settings = LocalSettings.current
        val dialogVisible by remember{ mutableStateOf(false) }
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
            Text(stringResource(R.string.settings_copy_all_codes_to_clipboard))
            Checkbox(checked = copy, onCheckedChange = {
                copy = !copy
                settings.putBoolean("copyAllCodes", copy)
            })
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