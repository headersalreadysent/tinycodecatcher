package co.ec.cnsyn.codecatcher.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.CodeCatcherPreview

@Composable
fun LazyIndicator(
    size: Int,
    scrollState: LazyListState,
    width: Int,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Horizontal = Arrangement.End,
    changed: (active: Int) -> Unit = { _ -> }
) {
    if (size > 1) {
        val mostVisibleItem by remember(scrollState, width) {
            derivedStateOf {
                return@derivedStateOf if (width <= scrollState.firstVisibleItemScrollOffset) scrollState.firstVisibleItemIndex + 1
                else scrollState.firstVisibleItemIndex
            }
        }
        LaunchedEffect(mostVisibleItem) {
            changed(mostVisibleItem)
        }
        Row(
            modifier = Modifier
                .then(modifier),
            horizontalArrangement = arrangement
        ) {
            (0..(size -1)).forEach { index ->
                val animatedWidth by animateDpAsState(
                    targetValue = if (mostVisibleItem == index) 30.dp else 10.dp,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    ),
                    label = "width"
                )
                val animatedColor by animateColorAsState(
                    targetValue = if (mostVisibleItem == index) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    ),
                    label = "color"
                )
                Box(
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .width(animatedWidth)
                        .height(6.dp)
                        .background(
                            animatedColor, shape = RoundedCornerShape(2.dp)
                        )
                ) {

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyIndicatorPreview() {
    CodeCatcherPreview {

        val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        LazyIndicator(size = 10, scrollState, 1500)
    }
}