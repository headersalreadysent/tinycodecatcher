package co.ec.cnsyn.codecatcher.pages.catcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
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
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatcherPage(model: CatcherPageViewModel = viewModel()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        Alignment.BottomCenter
    ) {

        val catchers by model.catchers.observeAsState(listOf())
        SkewSquare(
            skew = 30, modifier = Modifier
                .zIndex(2F)
                .navigationBarsPadding()
                .fillMaxWidth(),
            cut = "ts"
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 86.dp)
            ) {
                Text(text = "Catcher Count ${catchers.size}")
            }
        }
        val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .zIndex(1F),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState),
            verticalAlignment = Alignment.CenterVertically,
            userScrollEnabled = true,

            ) {
            items(catchers.size) {
                Column(modifier = Modifier
                    .fillParentMaxWidth(.9F)
                    .fillParentMaxHeight()) {
                    CatcherItem(catchers[it])
                }

            }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatcherItem(catcherDetail: CatcherDao.CatcherDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(4F)) {
                    Text(
                        text = "Gönderen",
                        style = infoTitleStyle
                    )
                    Text(
                        text = if (catcherDetail.catcher.sender == "") "Herkes" else catcherDetail.catcher.sender,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "İfade",
                        style = infoTitleStyle
                    )
                    Text(
                        text = catcherDetail.regex.regex,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    modifier = Modifier.weight(3F),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Yakalanan",
                        style = infoTitleStyle,
                        textAlign = TextAlign.End
                    )
                    AutoText(
                        text = catcherDetail.catcher.catchCount.toString(),
                        fontSize = 10..25,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
            ) {

                Text(
                    text = "Açıklama",
                    style = infoTitleStyle
                )
                Text(
                    text = catcherDetail.regex.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            catcherDetail.actions.forEach {
                ListItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    headlineContent = {
                        Text(text = it.detail.name)
                    },
                    leadingContent = {
                        IconName(name = it.detail.icon)
                    },
                    trailingContent = {
                        Checkbox(checked = true, onCheckedChange = {

                        })
                    }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            catcherDetail.codes.forEach { code ->
                ListItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    headlineContent = {
                        Text(text = code.code)
                    },
                    leadingContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                    }
                )
            }
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
