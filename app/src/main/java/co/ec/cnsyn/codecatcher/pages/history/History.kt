package co.ec.cnsyn.codecatcher.pages.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.pages.dashboard.LatestCode
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun History(model: HistoryViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val count by model.count.observeAsState(0)
        SkewSquare(
            skew = 30,
            cut = SkewSquareCut.TopStart,
            fill = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(3F)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_caught_count, count),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
        val history by model.history.observeAsState(listOf())
        val lazyState = rememberLazyListState()
        val uiController = rememberSystemUiController()
        val firstVisible by remember { derivedStateOf { lazyState.firstVisibleItemIndex } }
        val primary = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
        val surface = MaterialTheme.colorScheme.surface
        LaunchedEffect(firstVisible) {
            var darkIcons = ColorUtils.calculateLuminance(surface.toArgb()) > 0.5
            if (firstVisible > 0 && ColorUtils.calculateLuminance(primary.toArgb()) > 0.5) {
                darkIcons = true
            }
            uiController.setStatusBarColor(
                color = if (firstVisible == 0) Color.Transparent else primary,
                darkIcons = darkIcons
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1F)
                .padding(horizontal = 8.dp)
                .padding(bottom = 20.dp),
            state = lazyState
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                )
            }
            items(history.size) {
                val item = history[it]
                val lastMonth =
                    if (it == 0) "" else history[it - 1].code.date.dateString("MMM-YYYY")
                val month = item.code.date.dateString("MMM-YYYY")
                if (it == 0 || lastMonth != month) {
                    Text(
                        text = month,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.End,
                            fontSize = 11.sp
                        )
                    )
                }
                LatestCode(
                    item,
                    isLatest = it == history.size - 1
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 120.dp),
                    Alignment.TopCenter
                ) {
                    OutlinedButton(
                        enabled = count > history.size + 20,
                        onClick = {
                            model.loadMore()
                        }) {
                        Text(text = stringResource(R.string.history_load_more))
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    CodeCatcherPreview {
        History(MockHistoryViewModel())
    }
}