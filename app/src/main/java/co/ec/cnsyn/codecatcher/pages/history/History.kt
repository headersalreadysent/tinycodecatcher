package co.ec.cnsyn.codecatcher.pages.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.pages.dashboard.LatestCode
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun History(model: HistoryViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val count by model.count.observeAsState(0)
        val history by model.history.observeAsState(listOf())

        SkewSquare(
            skew = 30,
            cut = SkewSquareCut.TopStart,
            fill = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(3F)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.history_caught_count,
                        1,
                        history.sumOf { it.second.size },
                        count
                    ),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1F)
                .padding(horizontal = 8.dp)
                .padding(bottom = 20.dp)
        ) {
            history.forEach { part ->
                stickyHeader {
                    Text(
                        text = part.first,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .statusBarsPadding()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                items(part.second.size) {
                    val item = part.second[it]
                    LatestCode(
                        item,
                        isLatest = false
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 120.dp),
                    Alignment.TopCenter
                ) {
                    OutlinedButton(
                        enabled = count >= history.sumOf { it.second.size } + model.perPage,
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