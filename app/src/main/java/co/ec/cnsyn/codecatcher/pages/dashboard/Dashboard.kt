package co.ec.cnsyn.codecatcher.pages.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.CodeCatcherPreview
import co.ec.cnsyn.codecatcher.LocalNavigation
import co.ec.cnsyn.codecatcher.LocalSnackbar
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.Calendar
import co.ec.cnsyn.codecatcher.composables.DoughnutChart
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.LazyIndicator
import co.ec.cnsyn.codecatcher.composables.MiniIconStat
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.translate
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class
)
@Composable
fun Dashboard(model: DashboardViewModel = viewModel()) {

    val nav = LocalNavigation.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        val stat by model.stats.observeAsState(mapOf())
        SkewSquare(
            modifier = Modifier.zIndex(3F), skew = 30,
            fill = MaterialTheme.colorScheme.primaryContainer
        ) {
            var boxHeight by remember { mutableIntStateOf(0) }
            Box(modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    boxHeight = it.size.height
                }) {
                if (boxHeight != 0) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "indicator",
                        modifier = Modifier
                            .height(with(LocalDensity.current) { boxHeight.toDp() })
                            .align(Alignment.BottomEnd)
                            .zIndex(1F)
                            .alpha(.2F),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .zIndex(3F)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .weight(1.6F)
                            .padding(horizontal = 8.dp)
                    ) {
                        MiniIconStat(
                            modifier = Modifier.weight(1F),
                            title = stringResource(R.string.dashboard_stat_catcher_count),
                            content = (stat["catcher"] ?: 0).toString(),
                            icon = Icons.Default.Phishing
                        )
                        MiniIconStat(
                            modifier = Modifier.weight(1F),
                            title = stringResource(R.string.dashboard_stat_code_count),
                            icon = Icons.Default.DataObject,
                            content = (stat["code"] ?: 0).toString()
                        )
                    }
                    Row(modifier = Modifier.weight(2F)) {
                        val calendar by model.calendar.observeAsState(listOf())
                        Calendar(
                            stats = calendar,
                            numberInRow = 10,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {

                        }
                    }
                }
            }


        }
        var itemWidth by remember { mutableIntStateOf(1) }
        val verticalScrollState = rememberScrollState()
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(16.dp)
                .zIndex(2F)
                .graphicsLayer {
                    translationY = with(density) { 45.dp.toPx() * -1 }

                }
                .onGloballyPositioned {
                    itemWidth = (it.size.width * .8F).toInt()
                }
                .verticalScroll(verticalScrollState)
        ) {

            SkewSquare(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                cut = SkewSquareCut.TopStart,
                skew = 30,
                fill = MaterialTheme.colorScheme.secondaryContainer
            ) {

                val graphStat by model.graphStat.observeAsState(mapOf())
                val total = graphStat.keys.size
                if (total > 0) {
                    val graphListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LazyIndicator(total, graphListState, itemWidth)
                            Text(
                                text = stringResource(id = R.string.dashboard_list_catcher_stat),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                        var boxHeight by remember { mutableFloatStateOf(0F) }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                boxHeight = with(density) { it.size.height.toDp().value }
                            }) {

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                state = graphListState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = graphListState),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                items(graphStat.keys.size) {
                                    val key = graphStat.keys.toList()[it]
                                    DoughnutChart(
                                        modifier = Modifier
                                            .fillParentMaxWidth(),
                                        data = graphStat[key] ?: listOf(),
                                        title = translate("dashboard_graph_${key}_graph_title"),
                                        formatter = "%.0f"
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(boxHeight.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            Pair(
                                                0.0f,
                                                MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                            Pair(0.1f, Color.Transparent),
                                            Pair(0.9f, Color.Transparent),
                                            Pair(1f, MaterialTheme.colorScheme.secondaryContainer),
                                        )
                                    )
                            )
                        }
                    }
                }

            }
            val codes by model.codes.observeAsState(listOf())
            val height = LocalConfiguration.current.screenHeightDp.absoluteValue * .4F
            if (codes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(0.dp, height.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty), contentDescription = "emty codes icon",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(.5F)
                    )
                    Text(
                        text = stringResource(R.string.dashboard_no_code),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedButton(
                        onClick = {
                            model.generateTestSms()
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.dashboard_no_code_run_sample))
                    }
                }
            }
            if (codes.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(0.dp, height.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.dashboard_list_last_codes),
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    codes.forEachIndexed { index, item ->
                        LatestCode(
                            item,
                            isLatest = index == codes.size - 1
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {

                    TextButton(
                        onClick = {
                            nav.navigate("history")
                        },
                        contentPadding = PaddingValues(4.dp, 1.dp),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dashboard_see_history),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp),
                Alignment.Center
            ) {
                OutlinedButton(
                    onClick = {
                        nav.navigate("about")
                    },
                    modifier = Modifier.fillMaxWidth(.7F)
                ) {
                    Text(text = stringResource(id = R.string.dashboard_about))
                }
            }

        }
    }

}


/**
 * latest sms shower
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LatestCode(
    latest: CodeDao.Latest,
    isLatest: Boolean = false
) {

    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snack = LocalSnackbar.current
    val copiedMessage = stringResource(id = R.string.dashboard_list_last_codes_copied)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(if (isLatest) Modifier.padding(bottom = 16.dp) else Modifier)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                clipboardManager.setText(AnnotatedString(latest.code.code))
                scope.launch {
                    snack.showSnackbar(latest.code.code + " " + copiedMessage)
                }
            }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 4.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = latest.code.date.dateString("dd"),
                    style = MaterialTheme.typography.bodyMedium
                        .copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                )
                Text(
                    text = latest.code.date.dateString("MMM"),
                    style = MaterialTheme.typography.bodySmall
                        .copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                )
            }

            Column(
                modifier = Modifier.weight(5F)
            ) {
                latest.catcher?.let {
                    if (it.sender != "") {
                        Text(
                            text = it.sender,
                            modifier = Modifier.padding(bottom = 1.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .6F)
                            )
                        )
                    }
                }
                Text(
                    text = latest.code.code,
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = latest.code.sms,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            FlowRow(
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 4.dp)
                    .fillMaxHeight(),
                maxItemsInEachRow = if (latest.actions.size > 4) 3 else 2,
                verticalArrangement = Arrangement.Center,
            ) {
                latest.actions.forEach {
                    IconName(
                        name = it.detail.icon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    CodeCatcherPreview {
        Dashboard(MockDashboardViewModel())
    }
}