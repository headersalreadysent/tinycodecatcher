package co.ec.cnsyn.codecatcher.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
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
