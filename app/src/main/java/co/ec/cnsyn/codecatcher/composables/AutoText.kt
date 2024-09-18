package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.ec.cnsyn.codecatcher.LocalSettings
import kotlin.math.absoluteValue


@Composable
fun AutoText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: IntProgression = 10..50,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Visible,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    key: String? = null
) {
    var fontSizeValue by remember { mutableIntStateOf(fontSize.max()) }
    var lineHeightValue by remember { mutableFloatStateOf(fontSize.max().toFloat() * 1.4F) }
    var readyToDraw by remember { mutableStateOf(false) }


    val settings = LocalSettings.current
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeightValue.sp,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.sp,
        onTextLayout = { it: TextLayoutResult ->


            key?.let {
                val size = settings.getInt("fontSize-$key")
                if (size > 0) {
                    fontSizeValue = size
                    lineHeightValue = size * 1.4F
                    readyToDraw = true
                }
            }
            if (it.didOverflowHeight && !readyToDraw) {
                //calculate
                val nextFontSizeValue = (fontSizeValue.absoluteValue - fontSize.step)
                if (nextFontSizeValue <= fontSize.min().toFloat()) {
                    // Reached minimum, set minimum font size and it's readToDraw
                    fontSizeValue = fontSize.min()
                    readyToDraw = true
                } else {
                    fontSizeValue = nextFontSizeValue
                    lineHeightValue = nextFontSizeValue * 1.4F
                }
            } else {
                // Text fits before reaching the minimum, it's readyToDraw
                readyToDraw = true
                key?.let {
                    settings.putInt("fontSize-$key", fontSizeValue.absoluteValue)
                }
            }
        },
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() }
    )
}

@Preview(showBackground = true)
@Composable
fun AutoTextPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (i in 1..10) {
            val j = 10 - i
            Box(
                modifier = Modifier
                    .fillMaxWidth(j * 0.1.toFloat())
                    .height(60.dp)
                    .border(1.dp, Color.Red)
            ) {

                AutoText(
                    "$i hello this is very long text",
                    modifier = Modifier.fillMaxWidth(),
                    1..100 step 5,
                )
            }
        }

    }
}