package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@Composable
fun ToggleButton(
    value: Any,
    values: Map<Any, String>,
    modifier:Modifier = Modifier,
    onChange: (res: Any) -> Unit = { _ -> }
) {
    var selected by remember { mutableStateOf<Any>(value) }
    Row(
        modifier = Modifier.then(modifier)
    ) {
        values.toList().forEachIndexed { index,it ->
            val shape=if(index==0){
                RoundedCornerShape(6.dp, 0.dp, 0.dp, 6.dp);
            } else {
                if(index==values.size-1){
                    RoundedCornerShape(0.dp, 6.dp, 6.dp, 0.dp)
                } else {
                    RoundedCornerShape(0.dp)
                }
            }
            val colors=if(selected==it.first){
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
            Button(
                onClick = {
                    selected = it.first
                    onChange(it.first)
                },
                modifier = Modifier.height(32.dp),
                shape = shape,
                colors = colors,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(8.dp, 0.dp)
            ) {
                Text(
                    text = it.second,
                    modifier = Modifier.height(18.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ButtonGroupPreview() {
    CodeCatcherTheme {
        ToggleButton(
            value=0,
            values = mapOf(
                0 to "Passive",
                1 to "Active"
            )
        )
    }
}