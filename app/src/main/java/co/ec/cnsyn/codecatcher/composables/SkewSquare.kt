package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.geometry.Size
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
import kotlinx.serialization.json.JsonNull.content

enum class SkewSquareCut {
    TopStart, TopEnd, BottomStart, BottomEnd
}

@Composable
fun SkewSquare(
    modifier: Modifier = Modifier,
    fill: Color? = null,
    skew: Int = 16,
    cut: SkewSquareCut = SkewSquareCut.BottomEnd,
    content: @Composable () -> Unit = {},
) {
    val fillColor = fill ?: MaterialTheme.colorScheme.primaryContainer
    val height = with(LocalDensity.current) { skew.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawPath(
                    path = generatePath(size, cut, height, true),
                    color = Color.Gray.copy(alpha = .5F),
                    style = Fill
                )
                drawPath(
                    path = generatePath(size, cut, height),
                    color = fillColor,
                    style = Fill
                )

            }
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (cut == SkewSquareCut.TopStart || cut == SkewSquareCut.TopEnd) Modifier.padding(
                        top = skew.dp
                    ) else Modifier.padding(
                        bottom = skew.dp
                    )
                )

        ) {
            content()
        }
    }

}

fun generatePath(size: Size, cut: SkewSquareCut, height: Float, shadow: Boolean = false): Path {
    val shadowSize = (if (shadow) 2F else 0F)
    return Path().apply {
        when (cut) {
            SkewSquareCut.TopStart -> {
                moveTo(0f, height - shadowSize)
                lineTo(size.width, 0f - shadowSize)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            SkewSquareCut.TopEnd -> {
                moveTo(0f, 0f - shadowSize)
                lineTo(size.width, height - shadowSize)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            SkewSquareCut.BottomStart -> {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height + shadowSize)
                lineTo(0f, size.height - height + shadowSize)
                close()
            }

            else -> {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - height + shadowSize)
                lineTo(0f, size.height + shadowSize)
                close()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SkewsSquarePreview() {
    CodeCatcherTheme {
        Column {
            SkewSquare(skew = 30, cut = SkewSquareCut.TopStart) {
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(text = "hello Button")
                }

            }

            SkewSquare(skew = 30, cut = SkewSquareCut.TopEnd) {
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(text = "hello Button")
                }
            }
            SkewSquare(skew = 30, cut = SkewSquareCut.BottomStart) {
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(text = "hello Button")
                }
            }

            SkewSquare(skew = 30, cut = SkewSquareCut.BottomEnd) {
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(text = "hello Button")
                }
            }

        }

    }
}