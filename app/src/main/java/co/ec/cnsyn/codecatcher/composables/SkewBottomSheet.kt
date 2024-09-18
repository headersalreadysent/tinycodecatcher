package co.ec.cnsyn.codecatcher.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkewBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    skew: Int = 45,
    cut: SkewSquareCut = SkewSquareCut.TopEnd,
    fill: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(0.dp),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        contentWindowInsets = { WindowInsets.Companion.ime },
        dragHandle = {
            SkewSquare(
                skew = skew,
                cut = cut,
                fill = fill,
                tonalElevate = 0.dp
            ) {
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(fill)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .navigationBarsPadding()
        ) {
            content()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SkewBottomSheetPreview() {
    CodeCatcherTheme {
        SkewBottomSheet(
            onDismissRequest = { /*TODO*/ },
            sheetState = rememberModalBottomSheetState(

            )
        ) {
            Text("test")
        }
    }
}