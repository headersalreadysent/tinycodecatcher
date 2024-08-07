package co.ec.cnsyn.codecatcher.composables



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@Composable
fun IconName(
    name: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = "",
    tint: Color = LocalContentColor.current
) {
    var iconClassName = name
    if (!name.contains(".")) {
        iconClassName = "filled.$name"
    }
    val icon: ImageVector? = remember(name) { getIconByName(iconClassName) }
    Icon(
        icon ?: Icons.Filled.QuestionMark,
        contentDescription = contentDescription ?: name,
        modifier = modifier,
        tint = tint
    )
}


fun getIconByName(name: String): ImageVector? {
    var iconClassName = name
    if (!name.contains(".")) {
        iconClassName = "filled.$name"
    }
    val icon = try {
        val cl = Class.forName("androidx.compose.material.icons.${iconClassName}Kt")
        val method = cl.declaredMethods.first()
        val obj: Any = when (iconClassName.split(".")[0].lowercase()) {
            "filled" -> Icons.Filled
            "twotone" -> Icons.TwoTone
            "outlined" -> Icons.Outlined
            "rounded" -> Icons.Rounded
            "sharp" -> Icons.Sharp
            else -> Icons.Filled
        }
        method.invoke(null, obj) as ImageVector
    } catch (_: Throwable) {
        null
    }
    return icon
}


@Preview(showBackground = true)
@Composable
fun IconNamePreview(){
    CodeCatcherTheme {
        Row(modifier = Modifier.fillMaxWidth()){
            IconName(name = "Mic")
            IconName(name = "twotone.ContentCopy")
            IconName(name = "sharp.Add")
        }
    }
}
