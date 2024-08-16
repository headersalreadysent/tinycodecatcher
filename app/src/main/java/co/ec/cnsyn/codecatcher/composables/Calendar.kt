package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import kotlin.random.Random


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Calendar(
    stats: List<Int>,
    numberInRow: Int = 14,
    titleVisible: Boolean = true,
    color:Color = MaterialTheme.colorScheme.primary.copy(alpha = 0F),
    dayClick: (start: Int) -> Unit,
) {
    if (stats.isNotEmpty()) {
        val minDate = stats.min()
        val dayList = mutableMapOf<Long, Int>()
        //lets find end of today
        var date = unix()
        date = date - (date % 86400) + 86400
        var maxValue = 0;
        while (date > minDate) {
            val dayCount = stats.filter { it < date && it >= date - 86400 }.size
            dayList[date] = dayCount
            if (dayCount > maxValue) {
                maxValue = dayCount
            }
            date -= 86400
        }
        val density = LocalDensity.current
        var width by remember { mutableIntStateOf(0) }
        val boxWidth by remember(width) {
            var boxWidth = with(density) { (width.toFloat() / numberInRow.toFloat()).toDp() }
            boxWidth = (boxWidth.value - 1).dp
            mutableStateOf(boxWidth)
        }
        val flowWidth by remember(boxWidth) {
            mutableStateOf((boxWidth.value * numberInRow + numberInRow).dp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
        ) {
            val dayCount = ((unix() - minDate) / 86400) + 1
            if(titleVisible){
                Text(
                    text = stringResource(R.string.global_calendar_title, dayCount.toString()),
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = color.copy(alpha = .8F),
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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
                        dayList.toList().forEach { it ->
                            val dateValue = it.first
                            var count = 0
                            var boxColor=color
                            if (it.second > 0) {
                                val fraction = (it.second.toFloat() / maxValue.toFloat())
                                boxColor = color.copy(alpha = fraction)
                                count = it.second
                            }
                            Box(
                                Modifier
                                    .padding(.5.dp)
                                    .width(boxWidth)
                                    .aspectRatio(1F)
                                    .background(boxColor)
                                    .border(
                                        .5.dp,
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5F)
                                    )
                                    .clickable(enabled = count != 0) {
                                        dayClick(dateValue.toInt() - 86400)
                                    },
                                Alignment.Center
                            ) {
                                Text(
                                    text = if (count == 0) "" else count.toString(),
                                    color = color.copy(alpha = .8F),
                                    modifier = Modifier.alpha(.3F)
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
fun CalendarPreview() {
    CodeCatcherTheme {
        var now = unix()
        var list = List(60) {
            now -= (Random.nextFloat() * 86400).toInt()
            return@List now.toInt()
        }
        Calendar(list) { _ ->

        }
    }
}