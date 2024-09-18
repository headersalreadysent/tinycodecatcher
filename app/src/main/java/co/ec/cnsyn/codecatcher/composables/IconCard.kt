package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


@Composable
fun IconCard(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Done,
    height: Dp? = null,
    ratio: Float = 5F,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp),
    ) {
        var boxHeight by remember { mutableIntStateOf(0) }
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent), Alignment.Center){

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
                    contentDescription = "icon card for showing",
                    tint = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = .2F
                    ),
                    modifier = Modifier
                        .size(height ?: (boxHeight / ratio).dp)
                        .rotate(10F)
                        .zIndex(.1F)
                        .graphicsLayer {
                            translationX = size.width / 5F
                            translationY = size.height / 5F
                        }
                )
            }
            content()
        }

    }
}


@Composable
@Preview
fun IconCardPreview(){
    CodeCatcherTheme {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1F)){
            IconCard(icon = Icons.Filled.QuestionMark) {
                Text(text = "hello")
            }
        }
    }
}