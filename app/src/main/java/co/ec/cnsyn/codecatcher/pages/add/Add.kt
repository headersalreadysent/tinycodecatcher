package co.ec.cnsyn.codecatcher.pages.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.composables.AlertText
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.composables.SkewSwitch
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Add(model: AddViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {

            val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(bottom = 80.dp),
                state = scrollState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState),
                verticalAlignment = Alignment.CenterVertically,
                userScrollEnabled = false
            ) {
                item {
                    val regexes by model.regexes.observeAsState(listOf())
                    StepSender(regexes)
                }

            }

        }
        SkewSquare(
            modifier = Modifier.align(Alignment.BottomCenter),
            skew = 30,
            cut = SkewSquareCut.TopEnd
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "")
                    Text(text = "Önceki")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Sonraki")
                    Icon(Icons.Default.ChevronRight, contentDescription = "")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyItemScope.StepSender(
    regexes: List<Regex>
) {
    Card(
        modifier = Modifier
            .fillParentMaxWidth(.9F)
            .fillParentMaxHeight()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Set Catcher",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            var catcher by remember {
                mutableStateOf(Catcher())
            }
            LaunchedEffect(catcher) {
                println(catcher.toString())
            }
            var senderType by remember {
                mutableStateOf(0)
            }
            Text(text = "sender")
            SkewSwitch(value = listOf(Pair("Anybody", 0), Pair("Spesific", 1)),
                onChange = {
                    senderType = it as Int
                })
            AnimatedVisibility(visible = senderType == 1) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = catcher.sender,
                    onValueChange = {
                        catcher = catcher.copy(sender = it)
                    },
                    label = { Text(text = "Gönderen") },
                    trailingIcon = { Icon(Icons.Default.Person, contentDescription = "") },
                    placeholder = { Text(text = "Gönderen") }
                )
            }
            AlertText(
                text = "Tüm kullanıcılardan gelen mesajları yakalamak için gönderen alanını boş bırakabilirsiniz.",
                color = MaterialTheme.colorScheme.secondaryContainer,
            )

            var dropDownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = it },
            ) {
                var width by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .onGloballyPositioned {
                            width = with(density) { it.size.width.toDp() }
                        },
                    readOnly = true,
                    value = regexes.find { it.id == catcher.regexId }?.name ?: "",
                    onValueChange = {},
                    label = { Text(text = "Regex") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded) }
                )
                ExposedDropdownMenu(
                    modifier = Modifier.width(width),
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false },
                ) {
                    regexes.forEachIndexed { index, regex ->

                        Text(
                            text = buildAnnotatedString {
                                append(regex.name)
                                append(" ")
                                withStyle(SpanStyle(fontSize = 11.sp, color = Color.Gray)) {
                                    append(regex.regex)
                                }
                            },
                            modifier = Modifier
                                .width(width)
                                .clickable {
                                    catcher = catcher.copy(regexId = regex.id)
                                    dropDownExpanded = false
                                }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        if (index < regexes.size - 1) {
                            //don't show at end
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPreview(modifier: Modifier = Modifier) {
    CodeCatcherTheme {
        Add(MockAddViewModel())
    }
}