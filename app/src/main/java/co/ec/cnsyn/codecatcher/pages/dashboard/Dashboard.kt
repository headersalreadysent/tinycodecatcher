package co.ec.cnsyn.codecatcher.pages.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.StatCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun Dashboard(model: DashboardViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        val listState = rememberLazyListState()
        var scrollPosition by remember { mutableIntStateOf(0) }
        var titleHeight by remember { mutableIntStateOf(0) }

        LaunchedEffect(listState) {
            //listen scroll
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }
                .distinctUntilChanged()
                .map { (index, offset) -> index * 100 + offset }
                .collect { position -> scrollPosition = position }
        }
        SkewSquare(modifier = Modifier.zIndex(3F), skew = 30) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val cardModifier = Modifier
                    .aspectRatio(1F)
                    .weight(1F)
                StatCard(modifier = cardModifier, title = "catcher count", value = "50")
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(modifier = cardModifier, title = "catcher count", value = "50")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(16.dp)
                .zIndex(2F)
                .graphicsLayer {
                    translationY =
                        (if (scrollPosition < titleHeight) scrollPosition * -1 else titleHeight * -1).toFloat()
                }
        ) {
            //calculate space for translate
            val extraSpace = with(LocalDensity.current) { 30.dp.toPx() }.toInt()
            Text(text = "Last Codes", modifier = Modifier.onGloballyPositioned {
                titleHeight = it.size.height + extraSpace
            })
            LazyColumn(
                state = listState
            ) {
                items(13) { i ->
                    ListItem(
                        modifier = Modifier.padding(vertical = 8.dp),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        headlineContent = {
                            Text(text = "$i-322")

                        },
                        supportingContent = {
                            Text(text = "google")
                        })
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    CodeCatcherTheme {
        Dashboard(DashboardViewModel())
    }
}