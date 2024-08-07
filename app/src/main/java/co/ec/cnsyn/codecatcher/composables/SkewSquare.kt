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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.ColorUtils


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

                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height - height)
                        lineTo(0f, size.height)
                        close()
                    },
                    color = Color.Gray.copy(alpha = .3F),
                    style = Fill
                )
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height - height - 3)
                        lineTo(0f, size.height - 3)
                        close()
                    },
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