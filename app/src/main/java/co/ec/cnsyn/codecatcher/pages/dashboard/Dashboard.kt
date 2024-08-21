package co.ec.cnsyn.codecatcher.pages.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import com.google.accompanist.permissions.rememberPermissionState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
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
import co.ec.cnsyn.codecatcher.LocalSettings
import co.ec.cnsyn.codecatcher.LocalSnackbar
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.Calendar
import co.ec.cnsyn.codecatcher.composables.DoughnutChart
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.LazyIndicator
import co.ec.cnsyn.codecatcher.composables.MiniIconStat
import co.ec.cnsyn.codecatcher.composables.RealDevice
import co.ec.cnsyn.codecatcher.composables.SkewBottomSheet
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.helpers.unix
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun Dashboard(model: DashboardViewModel = viewModel()) {

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
                        contentDescription = "",
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
                val graphListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

                val graphStat by model.graphStat.observeAsState(mapOf())
                val total = graphStat.keys.size
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
                    var boxHeight by remember {
                        mutableFloatStateOf(0F)
                    }
                    val density = LocalDensity.current
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
                                val key = graphStat.keys.toList().get(it)
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
                                        Pair(0.0f, MaterialTheme.colorScheme.secondaryContainer),
                                        Pair(0.1f, Color.Transparent),
                                        Pair(0.9f, Color.Transparent),
                                        Pair(1f, MaterialTheme.colorScheme.secondaryContainer),
                                    )
                                )
                        )
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
                        painter = painterResource(id = R.drawable.empty), contentDescription = "",
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

            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp),
                Alignment.Center
            ) {
                val nav = LocalNavigation.current
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
    var settings = LocalSettings.current
    var permissionModel by remember { mutableStateOf(false) }
    val permissions by model.requiredPerms.observeAsState(listOf())
    LaunchedEffect(permissions) {
        val hiddenUntil = settings.getInt("permissionHidden", 0)
        if (hiddenUntil < unix() || hiddenUntil == 0) {
            permissionModel = permissions.isNotEmpty()
        }
    }

    if (permissionModel) {
        SkewBottomSheet(onDismissRequest = {
            permissionModel = false
            settings.putInt("permissionHidden", unix().toInt() + 43200)
        }, cut = SkewSquareCut.TopStart) {
            PermissionArea(permission = permissions) {
                model.calculatePermissions()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionArea(
    permission: List<DashboardViewModel.PermissionInfo>,
    then: (perm: String) -> Unit = { _ -> }
) {

    RealDevice {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_permission_needs_some_permission),
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                items(permission.size) {
                    val perm = permission[it]
                    val permState = rememberPermissionState(
                        permission = perm.permission,
                        onPermissionResult = {
                            then(perm.permission)
                        }
                    )
                    Button(
                        onClick = {
                            permState.launchPermissionRequest()
                        },
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        modifier = Modifier.padding(start = 4.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                perm.icon, contentDescription = perm.permission,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    )
                                    .scale(.8F),
                                tint = MaterialTheme.colorScheme.primary

                            )
                            Text(
                                text = perm.text,
                                modifier = Modifier.padding(start = 5.dp),
                                maxLines = 2
                            )

                        }
                    }
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