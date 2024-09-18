package co.ec.cnsyn.codecatcher.pages.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.LocalNavigation
import co.ec.cnsyn.codecatcher.LocalSnackbar
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.AlertText
import co.ec.cnsyn.codecatcher.composables.IconName
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.composables.SkewSwitch
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.htmlToAnnotatedString
import co.ec.cnsyn.codecatcher.helpers.rememberKeyboardVisibility
import co.ec.cnsyn.codecatcher.pages.catcher.ParamsDialog
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun Add(model: AddViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        val isKeyboardVisible by rememberKeyboardVisibility()
        val scrollState = rememberScrollState()
        var completeRatio by remember { mutableFloatStateOf(0F) }

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.TopStart)
                .zIndex(3F)
                .height(8.dp),
            color = MaterialTheme.colorScheme.secondary,
            progress = {
                completeRatio
            },
        )
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .then(
                    if (isKeyboardVisible) {
                        Modifier.padding(bottom = 170.dp)
                    } else {
                        Modifier.padding(bottom = 60.dp)
                    }
                )
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {

            val regexes by model.regexes.observeAsState(listOf())
            var catcher by remember { mutableStateOf(Catcher()) }
            var selectedRegex by remember { mutableStateOf<Regex?>(null) }
            var actionsDetails by remember { mutableStateOf<List<ActionDetail>>(listOf()) }
            LaunchedEffect(selectedRegex, catcher, actionsDetails) {
                var step = 0
                if (selectedRegex != null) {
                    step++
                }
                if (catcher.description != "") {
                    step++
                }
                if (actionsDetails.isNotEmpty()) {
                    step++
                }
                completeRatio = step.toFloat() / 3F
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                val scope = rememberCoroutineScope()
                StepSender(catcher, regexes) { catcherUpdate, regexUpdate ->
                    catcher = catcherUpdate
                    selectedRegex = regexUpdate
                }
                val actions by model.actions.observeAsState(listOf())
                StepActions(actionList = actions) { details ->
                    actionsDetails = details
                }
                val olderMessages by model.olderMessages.observeAsState(listOf())
                StepTest(olderMessages, selectedRegex) {
                    scope.launch {
                        delay(1500)
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
                val navigator = LocalNavigation.current
                val snackbar = LocalSnackbar.current
                val savedMessage = stringResource(id = R.string.add_set_save_message)
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .padding(bottom = 100.dp),
                    shape = RoundedCornerShape(3.dp),
                    enabled = completeRatio > .99F,
                    onClick = {
                        model.saveCatcher(
                            catcher = catcher,
                            actionDetails = actionsDetails,
                            { catcherId ->
                                navigator.navigate("catchers/$catcherId")
                                scope.launch {
                                    snackbar.showSnackbar(savedMessage,
                                        duration = SnackbarDuration.Long)
                                }
                            }
                        )
                    }) {
                    Icon(
                        Icons.Default.Save, contentDescription = "catcher save icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(id = R.string.add_set_save_button))

                }

            }

        }
        SkewSquare(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp),
            skew = 30,
            cut = SkewSquareCut.TopEnd,
            tonalElevate = 3.dp,
            fill = MaterialTheme.colorScheme.surface,
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepSender(
    catcherSource: Catcher,
    regexes: List<Regex>,
    update: (catcher: Catcher, regex: Regex?) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_set_cather_title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            //watch catcher and regex
            var catcher by remember { mutableStateOf(catcherSource) }
            var selectedRegex by remember { mutableStateOf<Regex?>(null) }
            //watch them and sent to page
            LaunchedEffect(catcher, selectedRegex) {
                update(catcher, selectedRegex)
            }
            var senderType by remember { mutableIntStateOf(0) }
            AlertText(
                text = stringResource(id = R.string.add_select_sender_type_alert),
                isHtml = true,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SkewSwitch(
                value = listOf(
                    Pair(
                        stringResource(R.string.add_catcher_select_everybody), 0
                    ), Pair(
                        stringResource(R.string.add_catcher_select_specific), 1
                    )
                ),
                icons = listOf(Icons.Default.Groups, Icons.Default.Person),
                onChange = {
                    senderType = it as Int
                })
            AnimatedVisibility(visible = senderType == 1) {

                val senderText = stringResource(id = R.string.add_catcher_set_catcher_sender)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = catcher.sender,
                    onValueChange = {
                        catcher = catcher.copy(sender = it)
                    },
                    label = { Text(text = senderText) },
                    trailingIcon = { Icon(Icons.Default.Person, contentDescription = "person") },
                    placeholder = { Text(text = senderText) }
                )
            }
            var dropDownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = it },
            ) {
                var width by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                val regexText = stringResource(id = R.string.add_catcher_set_catcher_regex)

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .onSizeChanged { size ->
                            width = with(density) { size.width.toDp() }
                        }
                        .onGloballyPositioned {
                            width = with(density) { it.size.width.toDp() }
                        },
                    readOnly = true,
                    value = regexes.find { it.id == catcher.regexId }?.name ?: "",
                    onValueChange = {},
                    label = { Text(text = regexText) },
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
                                    if (catcher.description == "") {
                                        catcher = catcher.copy(description = regex.description)
                                    }
                                    dropDownExpanded = false
                                    selectedRegex = regex
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
            val descriptionText = stringResource(id = R.string.add_catcher_set_catcher_description)

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = catcher.description,
                onValueChange = {
                    catcher = catcher.copy(description = it)
                },
                label = { Text(text = descriptionText) },
                placeholder = { Text(text = selectedRegex?.description ?: "") },
                maxLines = 3,
                minLines = 2
            )


        }
    }
}

@Composable
fun StepActions(
    actionList: List<Action>,
    update: (res: List<ActionDetail>) -> Unit = { _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_set_actions_title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            var actionMap by remember { mutableStateOf<Map<Int, ActionDetail>>(mapOf()) }
            //watch map to change it
            LaunchedEffect(actionMap) {
                update(actionMap.values.toList())
            }
            actionList.forEach { action ->
                val isHasParams = action.defaultParams != "{}"
                var enabled by remember { mutableStateOf(actionMap.keys.contains(action.id)) }
                var selectedActionDetail by remember { mutableStateOf<ActionDetail?>(null) }
                ParamsDialog(selectedActionDetail) { res ->
                    //close action
                    val mutable = actionMap.toMutableMap()
                    mutable[action.id] = res
                    actionMap = mutable.toMap()
                    selectedActionDetail = null
                    update(actionMap.values.toList())
                }
                ListItem(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable(enabled && isHasParams) {
                            //  actionParams(action)
                            selectedActionDetail = ActionDetail(
                                action = CatcherAction(
                                    catcherId = 0,
                                    actionId = action.id,
                                    params = action.defaultParams
                                ),
                                detail = action
                            )
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
                                Text(text = if (action.name == "") action.key else action.name)
                                if (isHasParams) {
                                    Icon(
                                        Icons.Filled.Settings, contentDescription = "add action",
                                        modifier = Modifier
                                            .height(16.dp)
                                            .padding(start = 5.dp),
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = .8F)
                                    )
                                }
                            }
                            if (action.description != "") {
                                Text(
                                    text = action.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    leadingContent = {
                        IconName(name = action.icon)
                    },
                    trailingContent = {
                        Checkbox(checked = enabled, onCheckedChange = {
                            val mutable = actionMap.toMutableMap()
                            if (enabled) {
                                mutable.remove(action.id)
                                enabled = false
                            } else {
                                //lets enable it
                                mutable[action.id] = ActionDetail(
                                    action = CatcherAction(
                                        catcherId = 0,
                                        actionId = action.id,
                                        params = action.defaultParams
                                    ),
                                    detail = action
                                )
                                enabled = true
                                if (isHasParams) {
                                    selectedActionDetail = mutable[action.id]
                                }
                            }
                            actionMap = mutable.toMap()
                        })
                    }
                )
            }


        }
    }
}

@Composable
fun StepTest(
    olderMessages: List<String>,
    selectedRegex: Regex? = null,
    scrollToEnd: () -> Unit = {  }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_set_test_title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            var tabIndex by remember { mutableIntStateOf(0) }

            val tabs = listOf("Older", "Test")
            var matchedMessages by remember { mutableStateOf<List<AnnotatedString>?>(null) }

            LaunchedEffect(selectedRegex) {
                selectedRegex?.let {
                    val searchPattern = it.regex.toPattern().toRegex()
                    val filtered = olderMessages.filter { it2 ->
                        return@filter searchPattern.containsMatchIn(it2)
                    }.map { it3 ->
                        val matches = searchPattern.findAll(it3).toList().first().value
                        return@map htmlToAnnotatedString(it3.replace(matches, "<b>$matches</b>"))
                    }
                    matchedMessages = filtered
                }
            }

            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                        }
                    )
                }
            }
            val config = LocalConfiguration.current
            when (tabIndex) {
                0 -> {
                    if (matchedMessages != null && matchedMessages!!.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            Alignment.Center
                        ) {

                            AlertText(
                                text = stringResource(id = R.string.add_catcher_no_match),
                                type = "warning",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    matchedMessages?.let { messages ->
                        LazyColumn(
                            Modifier
                                .height((config.screenHeightDp.absoluteValue * .30F).dp)
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        ) {
                            items(messages.size) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = messages[it],
                                        modifier = Modifier.padding(4.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                }

                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(
                                Dp.Unspecified,
                                (config.screenHeightDp.absoluteValue * .30F).dp
                            )
                    ) {
                        val label =
                            stringResource(id = R.string.add_catcher_set_catcher_test_area_text)
                        var value by remember { mutableStateOf("") }
                        AlertText(
                            text = stringResource(id = R.string.add_catcher_set_catcher_test_area_description),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        scrollToEnd()

                                    }
                                },
                            value = value,
                            onValueChange = {
                                value = it
                            },
                            label = { Text(text = label) },
                            placeholder = { Text(text = label) },
                            maxLines = 3,
                            minLines = 2
                        )
                        selectedRegex?.let {
                            var testResponse = buildAnnotatedString { append(value) }
                            val searchPattern = it.regex.toPattern().toRegex()
                            val matches = searchPattern.findAll(value).toList()
                            if (matches.isNotEmpty()) {
                                val match = matches.first().value
                                val parts = value.split(match)
                                testResponse = buildAnnotatedString {
                                    append(parts[0])
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append(match)
                                    }
                                    append(parts[1])
                                }

                            }
                            if (value.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (matches.isNotEmpty())
                                            Icons.Filled.CheckCircle else Icons.Filled.Error,
                                        contentDescription = "matches indicator",
                                        tint =
                                        if (matches.isNotEmpty())
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    Text(
                                        text = testResponse,
                                        modifier = Modifier.padding(4.dp),
                                        style = MaterialTheme.typography.bodyLarge
                                            .copy(
                                                textAlign = TextAlign.Justify
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
}

@Preview(showBackground = true)
@Composable
fun AddPreview() {
    CodeCatcherTheme {
        Add(MockAddViewModel())
    }
}