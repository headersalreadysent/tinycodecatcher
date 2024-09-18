package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import kotlin.math.atan2
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import co.ec.cnsyn.codecatcher.R
import java.util.Locale
import kotlin.random.Random


@Composable
fun DoughnutChart(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>,
    onSegmentClicked: (Int) -> Unit = {},
    title: String = "",
    formatter: String = "%.2f",
    defaultNumber: Float? = null,
    defaultText: String? = null
) {
    //get values as list
    val total = data.map { it.second }.sum()

    var tappedSegment by remember { mutableIntStateOf(-1) }
    var tappedDegree by remember { mutableDoubleStateOf(-1.0) }
    //color list for graph elements
    val colorList = generateColorShades(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        data.size
    )
    //inner content area
    val totalText = stringResource(R.string.doughnut_total_text)
    var textValue by remember { mutableStateOf(defaultText ?: totalText) }
    var numberValue by remember { mutableFloatStateOf(defaultNumber ?: total) }


    var graphHeight by remember { mutableFloatStateOf(0F) }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2F),
        Alignment.BottomCenter
    ) {

        Canvas(modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val coordX = tapOffset.x - size.width / 2F
                    val coordY = size.height - tapOffset.y
                    val angle = atan2(coordY, coordX)
                    tappedDegree = Math.toDegrees(angle.toDouble())
                    val targetDegree = -1 * tappedDegree
                    var startAngle = -180f
                    val oldTapped = tappedSegment
                    tappedSegment = -1
                    data.forEachIndexed { index, value ->
                        val sweepAngle = (value.second / total) * 180
                        val endAngle = startAngle + sweepAngle
                        if (startAngle <= targetDegree && targetDegree < endAngle) {
                            tappedSegment = index
                        }
                        startAngle = endAngle
                    }
                    if (tappedSegment != -1 && tappedSegment != oldTapped) {
                        onSegmentClicked(tappedSegment)
                        textValue = data[tappedSegment].first
                        numberValue = data[tappedSegment].second
                    }
                }
            }
        ) {
            graphHeight = size.height
            val canvasWidth = size.width
            val canvasHeight = size.height
            val strokeWidth = size.width * .1f
            val radius = (canvasWidth - (strokeWidth / 2)) * .4F
            val center = Offset(canvasWidth / 2, canvasHeight)
            var startAngle = -180f
            data.forEachIndexed { index, value ->
                var sweepAngle = (value.second / total) * 180
                val endAngle = startAngle + sweepAngle
                if (index < data.size - 1) {
                    sweepAngle = if (sweepAngle > 0) sweepAngle - 1 else sweepAngle
                }
                drawArc(
                    color = colorList[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = if (index == tappedSegment) strokeWidth * 1.1F else strokeWidth)
                )
                startAngle = endAngle
            }
        }
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = with(LocalDensity.current) { (graphHeight / 15).toDp() }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = textValue,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(.8F)
                )
            )
            Text(
                text =
                String.format(
                    Locale.getDefault(), formatter, numberValue
                ),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = with(LocalDensity.current) { (graphHeight / 4).toSp() },
                    color = MaterialTheme.colorScheme.primary
                )
            )
            if (title != "") {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary.copy(.8F)
                    )
                )
            }

        }
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun DoughnutChartPreview() {
    CodeCatcherTheme {

        Box(
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(2F)
        ) {
            DoughnutChart(
                Modifier,
                data = listOf(
                    Pair("Hello", Random.nextFloat() * 10),
                    Pair("Hello", Random.nextFloat() * 20)
                ),
                defaultNumber = 35F,
                defaultText = "Total",
                title = "Catcher count"
            )
        }


    }
}

fun generateColorShades(startColor: Color, endColor: Color, numShades: Int): List<Color> {
    val colors = mutableListOf<Color>()
    if (numShades < 3) {
        return listOf(startColor, endColor)
    }
    for (i in 0 until numShades) {
        val ratio = i.toFloat() / (numShades - 1)
        val color = blendColors(startColor, endColor, ratio)
        colors.add(color)
    }

    return colors
}

private fun blendColors(startColor: Color, endColor: Color, ratio: Float): Color {
    val startA = startColor.alpha
    val startR = startColor.red
    val startG = startColor.green
    val startB = startColor.blue

    val endA = endColor.alpha
    val endR = endColor.red
    val endG = endColor.green
    val endB = endColor.blue

    val a = (startA + ratio * (endA - startA))
    val r = (startR + ratio * (endR - startR))
    val g = (startG + ratio * (endG - startG))
    val b = (startB + ratio * (endB - startB))

    return Color(r, g, b, a)
}