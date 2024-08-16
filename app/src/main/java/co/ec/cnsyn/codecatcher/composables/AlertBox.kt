package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.ec.cnsyn.codecatcher.helpers.htmlToAnnotatedString
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@Composable
fun AlertText(
    text: String,
    modifier: Modifier = Modifier,
    type: String = "info",
    color: Color =
        MaterialTheme.colorScheme.tertiaryContainer,
    isHtml: Boolean = false,
) {
    val annotatedString = buildAnnotatedString {
        appendInlineContent("${type}Icon", "[info]")
        append(" ")
        if (isHtml) {
            htmlToAnnotatedString(text, this)
        } else {
            append(text)
        }
    }

    val types = listOf(
        Pair("infoIcon", Icons.Default.Info),
        Pair("warningIcon", Icons.Default.Warning),
        Pair("errorIcon", Icons.Default.Error),
        Pair("questionIcon", Icons.Default.QuestionMark)
    ).map { pair ->
        return@map Pair(pair.first, InlineTextContent(
            placeholder = Placeholder(
                width = 12.sp, height = 12.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                Icon(
                    imageVector = pair.second,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp), // size of the icon
                )
            }
        ))
    }.toMap()
    Text(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .background(color, RoundedCornerShape(1.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        inlineContent = types
    )

}


@Preview(showBackground = true)
@Composable
fun AlertPreview() {
    CodeCatcherTheme {
        AlertText(
            text = "hello"
        )
    }
}