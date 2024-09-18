package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    height: Dp? = null,
    ratio: Float = 5F,
    title: String = "",
    value: String = ""
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        var boxHeight by remember { mutableIntStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent), Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        boxHeight = it.size.height
                    },
                Alignment.BottomEnd
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(height ?: (boxHeight / ratio).dp)
                        .rotate(10F)
                        .zIndex(.1F)
                        .graphicsLayer {
                            translationX = size.width / 5F
                            translationY = size.height / 5F
                        },
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    title, modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {

                    AutoText(
                        key = "$title-$value",
                        text = value,
                        fontSize = 10..100 step 3,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth(.5F)
                    )
                }
            }
        }

    }
}


@Composable
@Preview
fun StatCardPreview() {
    CodeCatcherTheme {
        StatCard(icon = Icons.Filled.Add, title = "user", value = "25")

    }
}