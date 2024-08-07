package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

@Composable
fun SkewSquare(
    modifier: Modifier = Modifier,
    fill: Color? = null,
    skew: Int = 16,
    content: @Composable () -> Unit,
) {
    val fillColor = fill ?: MaterialTheme.colorScheme.primaryContainer
    var height = with(LocalDensity.current) { skew.dp.toPx() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height - height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = fillColor,
                    style = Fill
                )
            }
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = skew.dp)
        ) {
            content()
        }
    }

}


@Preview(showBackground = true)
@Composable
fun SkewsSquarePreview() {
    CodeCatcherTheme {
        SkewSquare(skew = 30) {
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.height(60.dp)
            ) {
                Text(text = "hello Button")
            }

        }
    }
}