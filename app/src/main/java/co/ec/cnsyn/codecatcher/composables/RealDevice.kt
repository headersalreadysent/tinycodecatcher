package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@Composable
fun RealDevice(
    content: @Composable () -> Unit = {},
) {
    if (LocalView.current.isInEditMode) {
        //if real mode set name
    } else {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun RealDevicePreview() {
    CodeCatcherTheme {
        RealDevice()
    }
}
