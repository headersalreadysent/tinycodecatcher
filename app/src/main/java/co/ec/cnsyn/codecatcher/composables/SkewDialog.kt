package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import kotlinx.serialization.json.JsonNull.content

enum class SkewDialogCut { Top, Bottom }

@Composable
fun SkewDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    minHeight: Dp = 0.dp,
    cut: SkewDialogCut = SkewDialogCut.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .padding(0.dp)
                .padding(bottom = 60.dp)
                .background(Color.Transparent),
        ) {
            if (cut == SkewDialogCut.Top){

                SkewSquare(
                    skew = 40, fill = containerColor,
                    cut =  SkewSquareCut.TopStart
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(0.dp, minHeight)
                    .background(containerColor)
                    .padding(16.dp)
            ) {
                content()
            }
            if (cut == SkewDialogCut.Bottom){
                SkewSquare(
                    skew = 40, fill = containerColor,
                    cut = SkewSquareCut.BottomEnd
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SkewDialogPreview() {
    CodeCatcherTheme {
        SkewDialog(onDismissRequest = { /*TODO*/ }) {
            Text("test")
        }
    }
}