package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
    tonalElevate: Dp = 0.dp,
    content: @Composable () -> Unit = {},
) {
    var fillColor = fill ?: MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        if (cut == SkewSquareCut.TopStart || cut == SkewSquareCut.TopEnd) {
            Skew(skew,cut,tonalElevate,fillColor)
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(fillColor),
            color = fillColor,
            tonalElevation = tonalElevate,
        ) {
            content()
        }

        if (cut == SkewSquareCut.BottomStart || cut == SkewSquareCut.BottomEnd) {
            Skew(skew,cut,tonalElevate,fillColor)
        }
    }

}

@Composable
private fun Skew(skew: Int, cut: SkewSquareCut, tonalElevate: Dp, fillColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(skew.dp)
    ) {
        val gray = Color.Gray.copy(alpha = .1F)
        val shadowShape = generateShape(cut, true)
        val shape = generateShape(cut)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(gray, shadowShape)
                .clip(shadowShape)
                .alpha(.8F),
            color = gray,
            tonalElevation = tonalElevate,
        ) {}
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(fillColor, shape)
                .clip(shape),
            color = fillColor,
            tonalElevation = tonalElevate,
        ) {}
    }
}

@Composable
fun generateShape(cut: SkewSquareCut, shadow: Boolean = false): Shape {
    val shadowSize = with(LocalDensity.current) { if (shadow) -2.dp.toPx() else 0F }
    return when (cut) {
        SkewSquareCut.TopStart -> TopStartShape(shadowSize)
        SkewSquareCut.TopEnd -> TopEndShape(shadowSize)
        SkewSquareCut.BottomStart -> BottomStartShape(shadowSize)
        SkewSquareCut.BottomEnd -> BottomEndShape(shadowSize)
    }
}


class TopStartShape(var shadowSize: Float = 0F) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height + shadowSize)
            lineTo(size.width, 0f + shadowSize)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

class TopEndShape(var shadowSize: Float = 0F) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f + shadowSize)
            lineTo(size.width, size.height + shadowSize)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}


class BottomStartShape(var shadowSize: Float = 0F) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - shadowSize)
            lineTo(0f, -shadowSize)
            close()
        }
        return Outline.Generic(path)
    }
}

class BottomEndShape(var shadowSize: Float = 0F) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, -shadowSize)
            lineTo(0f, size.height - shadowSize)
            close()
        }
        return Outline.Generic(path)
    }
}

@Preview(showBackground = true)
@Composable
fun SkewsSquarePreview() {
    CodeCatcherTheme {
        Column(modifier = Modifier.background(Color.Red)) {
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