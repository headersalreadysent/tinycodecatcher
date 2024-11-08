package co.ec.cnsyn.codecatcher.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

class LeftSkew : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo((size.width / 55F) * 55F, 0f)
            lineTo((size.width / 55F) * 45F, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

class RightSkew : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo((size.width / 55F) * 10F, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo((size.width / 55F) * 0F, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun SkewSwitch(
    value: List<Pair<String, Any>>,
    icons: List<ImageVector> = listOf(),
    selectedItem: Int = 0,
    onChange: (value: Any) -> Unit = { _ -> },
    activeColor: Color = MaterialTheme.colorScheme.primary,
    passiveColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    activeTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    passiveTextColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    animateTime: Int = 200
) {
    val density = LocalDensity.current
    var width by remember { mutableIntStateOf(selectedItem) }
    var selected by remember { mutableStateOf(0) }
    val leftActiveColor by animateColorAsState(
        targetValue = if (selected == 0) activeColor else passiveColor,
        animationSpec = tween(animateTime, easing = LinearEasing),
        label = "lb"

    )
    val rightActiveColor by animateColorAsState(
        targetValue = if (selected == 1) activeColor else passiveColor,
        animationSpec = tween(animateTime, easing = LinearEasing),
        label = "rb"
    )
    val leftTextColor by animateColorAsState(
        targetValue = if (selected == 0) activeTextColor else passiveTextColor,
        animationSpec = tween(animateTime, easing = LinearEasing),
        label = "lt"
    )
    val rightTextColor by animateColorAsState(
        targetValue = if (selected == 1) activeTextColor else passiveTextColor,
        animationSpec = tween(animateTime, easing = LinearEasing),
        label = "rt"
    )

    with(density) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.secondary)
                .padding(3.dp)
                .onGloballyPositioned {
                    width = it.size.width
                },
        ) {
            Box(
                modifier = Modifier
                    .width((width * .55F).toDp())
                    .clip(LeftSkew())
                    .background(leftActiveColor)
                    .align(Alignment.CenterStart)
                    .clickable {
                        selected = 0
                        onChange(value[0].second)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icons.isNotEmpty()) {
                        Icon(
                            icons[0], contentDescription = value[0].first,
                            modifier = Modifier.padding(end = 8.dp),
                            tint = leftTextColor
                        )
                    }
                    Text(
                        text = value[0].first,
                        color = leftTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width((width * .55F).toDp())
                    .clip(RightSkew())
                    .background(rightActiveColor)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        selected = 1
                        onChange(value[1].second)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = value[1].first,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = rightTextColor,
                        textAlign = TextAlign.End
                    )
                    if (icons.size > 1) {
                        Icon(
                            icons[1], contentDescription = value[1].first,
                            modifier = Modifier.padding(start = 8.dp),
                            tint = rightTextColor
                        )
                    }
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun SkewSwitchPreview() {
    CodeCatcherTheme {
        SkewSwitch(
            value = listOf(
                Pair("User", "user"),
                Pair("EveryBody", "")
            ),
            icons = listOf(Icons.Default.Groups, Icons.Default.Person)
        )

    }
}