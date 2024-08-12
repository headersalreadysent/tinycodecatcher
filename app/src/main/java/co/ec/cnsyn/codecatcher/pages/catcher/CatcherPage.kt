package co.ec.cnsyn.codecatcher.pages.catcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.composables.AutoText
import co.ec.cnsyn.codecatcher.composables.Calendar
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.SkewBottomSheet
import co.ec.cnsyn.codecatcher.composables.SkewDialog
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.composables.StatCard
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.sms.ActionRunner
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import co.ec.cnsyn.codecatcher.values.actionList
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CatcherPage(model: CatcherPageViewModel = viewModel()) {


    // bottom sheet related
    val sheetState = rememberModalBottomSheetState()
    var showAddActionSheet by remember { mutableStateOf(false) }
    var dayDetailSheet by remember { mutableStateOf(false) }
    var selectedCatcher by remember { mutableStateOf<CatcherDao.CatcherDetail?>(null) }
    var selectedActionDetail by remember { mutableStateOf<ActionDetail?>(null) }
    //list of all actions in app
    val actions by model.allActions.observeAsState(listOf())


    Box(
        modifier = Modifier.fillMaxSize(),
        Alignment.BottomCenter
    ) {

        val catchers by model.catchers.observeAsState(listOf())
        val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

        var itemWidth by remember {
            mutableIntStateOf(1)
        }
        val mostVisibleItem = remember(scrollState, itemWidth) {
            derivedStateOf {
                return@derivedStateOf if (itemWidth <= scrollState.firstVisibleItemScrollOffset) scrollState.firstVisibleItemIndex + 1
                else scrollState.firstVisibleItemIndex
            }
        }
        SkewSquare(
            skew = 30, modifier = Modifier
                .zIndex(2F)
                .navigationBarsPadding()
                .fillMaxWidth()
                .onGloballyPositioned {
                    itemWidth = (it.size.width * .8F).toInt()
                },
            cut = SkewSquareCut.TopStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 86.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                catchers.forEachIndexed { index, _ ->
                    val animatedWidth by animateDpAsState(
                        targetValue = if (mostVisibleItem.value == index) 30.dp else 10.dp,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "width"
                    )
                    val animatedColor by animateColorAsState(
                        targetValue = if (mostVisibleItem.value == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "color"
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .width(animatedWidth)
                            .height(6.dp)
                            .background(
                                animatedColor,
                                shape = RoundedCornerShape(2.dp)
                            )
                    ) {

                    }
                }
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .zIndex(1F),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(catchers.size) {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth(.9F)
                        .fillParentMaxHeight()
                ) {
                    CatcherItem(
                        catcherDetail = catchers[it],
                        allActions = actions,
                        isActive = it == mostVisibleItem.value,
                        addAction = { catcher ->
                            selectedCatcher = catcher
                            showAddActionSheet = true
                        },
                        changeStatus = { action, status ->
                            model.actionStatus(catchers[it].catcher.id, action, status)
                        },
                        dayDetail = { catcherId, start ->
                            model.loadDayStats(catcherId, start)
                            dayDetailSheet = true
                        },
                        actionParams = { actionDetail ->
                            selectedActionDetail = actionDetail
                        }
                    )
                }

            }
        }
    }

    if (showAddActionSheet) {
        SkewBottomSheet(
            onDismissRequest = {
                showAddActionSheet = false
            },
            sheetState = sheetState
        ) {
            selectedCatcher?.let { catcher ->
                //show add bottom sheet for catcher
                AddActionToCatcherBottomSheet(
                    catcherDetail = catcher,
                    actions = actions,
                    changeStatus = { action, status ->
                        model.actionStatus(catcher.catcher.id, action, status)
                    },
                    onDismissRequest = {
                        showAddActionSheet = false
                    },
                )
            }
        }
    }
    val dayCodes by model.dayCodes.observeAsState(listOf())
    if (dayDetailSheet) {
        DayModalBottom(
            dayCodes = dayCodes, close = {
                dayDetailSheet = false
                model.clearDayStat()
            },
            sheetState = sheetState
        )
    }
    ParamsDialog(selectedActionDetail) {
        //close action
        selectedActionDetail = null
    }
}

@Composable
fun CatcherItem(
    catcherDetail: CatcherDao.CatcherDetail,
    allActions: List<Action>,
    isActive: Boolean = false,
    addAction: (catherDetail: CatcherDao.CatcherDetail) -> Unit = { _ -> },
    changeStatus: (action: Action, status: Boolean) -> Unit,
    dayDetail: (catcherId: Int, start: Int) -> Unit,
    actionParams: (actionDetail: ActionDetail) -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isActive) 1F else .8F,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "alpha"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 90.dp)
            .alpha(animatedAlpha)
    ) {
        CatcherTopCard(catcherDetail)
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            catcherDetail.actions.forEach { action ->
                val isHasParams = action.detail.defaultParams != "{}"

                var enabled by remember(action) {
                    mutableStateOf(action.action.status == 1)
                }
                //show every action in list
                ListItem(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable(enabled && isHasParams) {
                            actionParams(action)
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    headlineContent = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (action.detail.name == "") action.detail.key else action.detail.name)
                                if (isHasParams) {
                                    Icon(
                                        Icons.Filled.Settings, contentDescription = "",
                                        modifier = Modifier
                                            .height(16.dp)
                                            .padding(start = 5.dp),
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = .8F)
                                    )
                                }
                            }
                            if (action.detail.description != "") {
                                Text(
                                    text = action.detail.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    leadingContent = {
                        IconName(name = action.detail.icon)
                    },
                    trailingContent = {
                        Checkbox(checked = enabled, onCheckedChange = {
                            changeStatus(action.detail, !enabled)
                            enabled = !enabled
                        })
                    }
                )
            }
            if (catcherDetail.actions.size < allActions.size) {
                //if there is missing action
                OutlinedButton(
                    onClick = { addAction(catcherDetail) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Aksiyon ekle")
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))

        CatcherStartArea(catcherDetail = catcherDetail, dayDetail = dayDetail)


    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatcherTopCard(catcherDetail: CatcherDao.CatcherDetail) {
    val infoTitleStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Gönderen", style = infoTitleStyle)
                Text(
                    if (catcherDetail.catcher.sender == "") "Herkes" else catcherDetail.catcher.sender,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("İfade", style = infoTitleStyle)
                FlowRow {
                    Text(catcherDetail.regex.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        catcherDetail.regex.regex,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.typography.bodyLarge.color.copy(alpha = .8F)
                        ),
                    )
                }
            }

            Box(modifier = Modifier.weight(1F), Alignment.TopEnd) {
                Column(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Yakalanan",
                            style = infoTitleStyle.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                            ),
                            textAlign = TextAlign.End
                        )
                        AutoText(
                            text = catcherDetail.catcher.catchCount.toString(),
                            fontSize = 10..25,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    SkewSquare(
                        cut = SkewSquareCut.BottomStart,
                        skew = 15,
                        fill = MaterialTheme.colorScheme.primary
                    )
                }

            }


        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
        ) {

            Text("Açıklama", style = infoTitleStyle)
            Text(catcherDetail.regex.description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun CatcherStartArea(
    catcherDetail: CatcherDao.CatcherDetail,
    dayDetail: (catcherId: Int, start: Int) -> Unit
) {
    Column {
        Text(
            text = "Ortalamalar",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            listOf(7, 14, 30).forEach {
                StatCard(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1F)
                        .aspectRatio(1.4F),
                    title = "$it Gün",
                    icon = Icons.Default.Timeline,
                    value = String.format(Locale.getDefault(), "%.2f", catcherDetail.avg[it] ?: 0F)
                )
            }
        }

        Calendar(catcherDetail.stat) {
            dayDetail(catcherDetail.catcher.id, it)
        }

    }
}

@Composable
fun AddActionToCatcherBottomSheet(
    catcherDetail: CatcherDao.CatcherDetail,
    actions: List<Action>,
    changeStatus: (action: Action, status: Boolean) -> Unit,
    onDismissRequest: () -> Unit = { -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        val added = catcherDetail.actions.map { it.action.actionId }
        var recorded by remember { mutableStateOf<List<Int>>(listOf()) }
        val remainActions = actions.filter {
            !added.contains(it.id) && !recorded.contains(it.id)
        }
        LaunchedEffect(remainActions) {
            if (remainActions.isEmpty()) {
                onDismissRequest()
            }
        }
        remainActions.forEach { action ->
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable {
                        changeStatus(action, true)
                        val list = recorded.toMutableList()
                        list.add(action.id)
                        recorded = list.toList()
                    },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                headlineContent = {
                    Column {
                        Text(text = if (action.name == "") action.key else action.name)
                        Text(
                            text = if (action.description == "") action.key else action.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                leadingContent = {
                    IconName(name = action.icon)
                },
            )

        }
    }

}

@Composable
fun ParamsDialog(
    action: ActionDetail?,
    close: () -> Unit = { -> }
) {
    if (action == null || action.detail.defaultParams == "{}") {
        return
    }
    SkewDialog(
        modifier = Modifier,
        onDismissRequest = {
            close()
        },
    ) {
        Text(
            text = action.detail.name,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(text = action.detail.description)
        val instance = ActionRunner.getActionInstance(action.detail.action)
        instance?.let {
            instance.Settings(action) {

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayModalBottom(dayCodes: List<Code>, close: () -> Unit, sheetState: SheetState) {
    SkewBottomSheet(
        onDismissRequest = {
            close()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            var text = "Günlük Yakalananlar"
            if (dayCodes.isNotEmpty()) {
                text += " (${dayCodes[0].date.dateString("dd.MM.YYYY")})"
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall
            )


            if (dayCodes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    Alignment.Center
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxHeight(.8F)
                    )
                }
            }
            if (dayCodes.isNotEmpty()) {
                dayCodes.forEach { code ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                    text = code.date.dateString("dd"),
                                    style = MaterialTheme.typography.bodySmall
                                        .copy(
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                )
                                Text(
                                    text = code.date.dateString("MMM"),
                                    style = MaterialTheme.typography.bodySmall
                                        .copy(
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                )
                            }
                            Column(
                                modifier = Modifier.weight(5F)
                            ) {
                                if (code.sender != "") {
                                    Text(
                                        text = code.sender,
                                        modifier = Modifier.padding(bottom = 1.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                alpha = .6F
                                            )
                                        )
                                    )
                                }

                                Text(
                                    text = code.code,
                                    modifier = Modifier.padding(bottom = 2.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = code.sms,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CatcherPagePreview() {
    CodeCatcherTheme {
        CatcherPage(MockCatcherViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun CatcherPageTopCardPreview() {
    CodeCatcherTheme {
        val model = MockCatcherViewModel()
        model.catchers.value?.let {
            CatcherTopCard(it[0])
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CatcherPageAddActionPreview() {
    CodeCatcherTheme {
        val model = MockCatcherViewModel()
        AddActionToCatcherBottomSheet(
            model.catchers.value!![0],
            actions = actionList(),
            changeStatus = { _, _ ->
            },
            onDismissRequest = {}
        )
    }
}
