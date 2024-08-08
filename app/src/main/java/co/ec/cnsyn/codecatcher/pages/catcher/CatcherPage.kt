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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
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
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import co.ec.cnsyn.codecatcher.values.actionList
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CatcherPage(model: CatcherPageViewModel = viewModel()) {


    // bottom sheet related
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCatcher by remember { mutableStateOf<CatcherDao.CatcherDetail?>(null) }
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
            cut = "ts"
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
                            showBottomSheet = true
                        }, changeStatus = { action, status ->
                            model.actionStatus(catchers[it], action, status)
                        })
                }

            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(0.dp), // Fixed corner radius
            containerColor = Color.Transparent,
            dragHandle = {
                SkewSquare(
                    skew = 45,
                    cut = "te",
                    fill = MaterialTheme.colorScheme.surface
                )
            }
        ) {
            selectedCatcher?.let { catcher ->
                //show add bottom sheet for catcher
                AddActionToCatcherBottomSheet(
                    catcherDetail = catcher,
                    actions = actions,
                    changeStatus = { action, status ->
                        model.actionStatus(catcher, action, status)
                    },
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                )
            }
        }
    }

}

@Composable
fun CatcherItem(
    catcherDetail: CatcherDao.CatcherDetail,
    allActions: List<Action>,
    isActive: Boolean = false,
    addAction: (catherDetail: CatcherDao.CatcherDetail) -> Unit = { _ -> },
    changeStatus: (action: Action, status: Boolean) -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isActive) 1F else .5F,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
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

                var enabled by remember(action) {
                    mutableStateOf(action.action.status == 1)
                }
                ListItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    headlineContent = {
                        Column {
                            Text(text = if (action.detail.name == "") action.detail.key else action.detail.name)
                            Text(
                                text = if (action.detail.description == "") action.detail.key else action.detail.description,
                                style = MaterialTheme.typography.bodySmall
                            )
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
                OutlinedButton(
                    onClick = {
                        addAction(catcherDetail)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Aksiyon ekle")
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Calendar(catcherDetail.stat)

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Calendar(stats: List<CodeDao.Stat>) {
    var maxDate = stats.first().start
    var minDate = stats.last().start


    var maxValue = stats.maxByOrNull { it.count }?.count ?: 0

    val density = LocalDensity.current
    var width by remember {
        mutableIntStateOf(0)
    }
    val boxWidth by remember(width) {
        var boxWidth = with(density) { (width.toFloat() / 14F).toDp() }
        boxWidth = (boxWidth.value - 1).dp
        mutableStateOf(boxWidth)
    }
    val flowWidth by remember(boxWidth) {
        mutableStateOf((boxWidth.value * 14 + 14).dp)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(text = "Yakalama Dağılımı")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    width = it.size.width
                },
            Alignment.Center
        ) {
            FlowRow(
                modifier = Modifier
                    .width(flowWidth),
                horizontalArrangement = Arrangement.Start
            ) {
                if (width > 0) {
                    var date = maxDate
                    while (date >= minDate) {
                        var dateValue = date;
                        var item =
                            stats.find { it.start > dateValue && it.start <= dateValue + 86400 }

                        var color = MaterialTheme.colorScheme.primary.copy(alpha = 0F)
                        val fraction = 1F - ((item?.count ?: 0).toFloat() / maxValue.toFloat())
                        if (item != null) {
                            color = color.copy(alpha = fraction)
                        }
                        Box(
                            Modifier
                                .padding(.5.dp)
                                .width(boxWidth)
                                .aspectRatio(1F)
                                .background(color)
                                .border(
                                    .5.dp,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5F)
                                )
                        ) {
                            /*Text(
                                text = (item?.count ?: "0").toString(),
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center
                            )*/

                        }
                        date = date - 86400

                    }
                }


            }
        }
    }

}


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
                    .weight(4F)
                    .padding(16.dp)
            ) {
                Text("Gönderen", style = infoTitleStyle)
                Text(
                    if (catcherDetail.catcher.sender == "") "Herkes" else catcherDetail.catcher.sender,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("İfade", style = infoTitleStyle)
                Row {
                    Text(catcherDetail.regex.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        catcherDetail.regex.regex,
                        modifier=Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.typography.bodyLarge.color.copy(alpha = .8F)
                        ),
                    )
                }
            }

            Box(modifier = Modifier.weight(3F), Alignment.TopEnd) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary,
                            shape = GenericShape { size, _ ->
                                addRoundRect(
                                    RoundRect(
                                        rect = Rect(offset = Offset(0f, 0f), size = size),
                                        bottomLeft = CornerRadius(40F),
                                    )
                                )
                            })
                        .padding(16.dp),
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

@Preview(showBackground = true)
@Composable
fun CatcherPagePreview(): Unit {
    CodeCatcherTheme {
        CatcherPage(MockCatcherViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun CatcherPageTopCardPreview(): Unit {

    CodeCatcherTheme {
        val model = MockCatcherViewModel()
        model.catchers.value?.let {

            CatcherTopCard(it[0])
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CatcherPageAddActionPreview(): Unit {
    CodeCatcherTheme {
        val model = MockCatcherViewModel()
        AddActionToCatcherBottomSheet(
            model.catchers.value!!.get(0),
            actions = actionList(),
            changeStatus = { action, status ->
            },
            onDismissRequest = {}
        )
    }
}

