package co.ec.cnsyn.codecatcher.pages.dashboard

import android.Manifest
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.Calendar
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.MiniIconStat
import co.ec.cnsyn.codecatcher.composables.RealDevice
import co.ec.cnsyn.codecatcher.composables.SkewBottomSheet
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.composables.StatCard
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.translate
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonNull.content

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
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

        val stat by model.stats.observeAsState(mapOf())

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
                    .statusBarsPadding()
                    .fillMaxWidth()
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
                    Calendar(stats = calendar, numberInRow = 10) {

                    }
                }
            }

        }

        var extraSpace = 0
        var padding = 0

        with(LocalDensity.current) {
            extraSpace = 50.dp.toPx().toInt()
            padding = 20.dp.toPx().toInt() * -1
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(16.dp)
                .zIndex(2F)
                .graphicsLayer {
                    translationY =
                        (if (scrollPosition < titleHeight) scrollPosition * -1 else titleHeight * -1).toFloat() + padding
                }
        ) {
            val codes by model.codes.observeAsState(listOf())
            //calculate space for translate
            Text(
                text = stringResource(id = R.string.dashboard_list_last_codes),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        titleHeight = it.size.height + extraSpace
                    },
                style = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.End
                )
            )

            LazyColumn(
                state = listState,
            ) {
                items(codes.size) { i ->
                    LatestCode(
                        codes[i],
                        isLatest = i == codes.size - 1
                    )

                }
            }

        }
    }


    val permission by model.requiredPerms.observeAsState(listOf())
    if (permission.isNotEmpty()) {
        SkewBottomSheet(onDismissRequest = {
        }, cut = SkewSquareCut.TopStart) {
            PermissionArea(permission = permission)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionArea(permission: List<DashboardViewModel.PermissionInfo>) {

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
                    val permState = rememberPermissionState(permission = perm.permission)
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
                            Icon(perm.icon, contentDescription = perm.permission,
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
@OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
@Composable
fun LatestCode(
    latest: CodeDao.Latest,
    isLatest: Boolean = false
) {

    val clipboardManager = LocalClipboardManager.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(if (isLatest) Modifier.padding(bottom = 16.dp) else Modifier)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                clipboardManager.setText(AnnotatedString(latest.code.code))
            }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxSize()
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
                    style = MaterialTheme.typography.bodySmall
                        .copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                )
                Text(
                    text = latest.code.date.dateString("MMM"),
                    style = MaterialTheme.typography.bodySmall
                        .copy(
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
                            text = it.sender ?: "",
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
                    style = MaterialTheme.typography.bodyLarge
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
                maxItemsInEachRow = 2,
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center
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
    CodeCatcherTheme {
        Dashboard(MockDashboardViewModel())
    }
}