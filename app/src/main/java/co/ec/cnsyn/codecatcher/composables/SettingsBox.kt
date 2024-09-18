package co.ec.cnsyn.codecatcher.composables

import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ParamValueBox(
    label: String,
    value: String,
    helperText: String? = null,
    keyboardType: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    valueChange: (res: String) -> Unit = { _ -> },
) {
    var textValue by remember { mutableStateOf(value) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textValue,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                    )
                )
            },
            onValueChange = {
                textValue = it
                valueChange(textValue)
            },
            keyboardOptions = keyboardType
        )

        ParamHelperArea(helperText)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamOptionBox(
    label: String,
    value: String,
    alternatives: List<Pair<String, String>> = listOf(),
    helperText: String? = null,
    valueChange: (res: String) -> Unit = { _ -> },
) {
    val selected = alternatives.find { it.first == value }?.second ?: value
    var textValue by remember { mutableStateOf(selected) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {

        var dropDownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            expanded = dropDownExpanded,
            onExpandedChange = { dropDownExpanded = it },
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = textValue,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = {},
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded) }
            )
            ExposedDropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(.88F),
                expanded = dropDownExpanded,
                onDismissRequest = { dropDownExpanded = false },
            ) {
                alternatives.forEachIndexed { index, alternative ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            textValue = alternative.second
                            valueChange(alternative.first)
                            dropDownExpanded = false
                        }
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            alternative.second,
                            modifier = Modifier.weight(1F),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (index < alternatives.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
        ParamHelperArea(helperText)

    }
}

@Composable
fun ParamHelperArea(helperText: String? = "") {
    if (helperText == null || helperText == "") {
        return
    }
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8F)
    Row(
        modifier = Modifier.fillMaxWidth().padding(top=4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Lightbulb,
            contentDescription = "param helper text",
            modifier = Modifier.size(12.dp),
            tint = color,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = helperText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color
            )
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun MiniUpdateBoxPreview() {
    CodeCatcherTheme {
        Column {
            ParamValueBox("label", "hello", "Must contains param info")
            ParamOptionBox("label", "hello", listOf(Pair("hello", "HELLO"), Pair("world", "WORLD")))
        }

    }
}