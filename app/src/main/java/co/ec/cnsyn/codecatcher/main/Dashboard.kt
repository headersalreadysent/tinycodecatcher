package co.ec.cnsyn.codecatcher.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import co.ec.cnsyn.codecatcher.composables.AutoText
import co.ec.cnsyn.codecatcher.composables.IconCard
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.composables.SkewSquare
import co.ec.cnsyn.codecatcher.composables.StatCard

@Composable
fun Dashboard(model: DashboardViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .statusBarsPadding()
    ) {
        SkewSquare {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val cardModifier = Modifier
                    .aspectRatio(1F)
                    .weight(1F)
                StatCard(modifier = cardModifier, title = "catcher count", value = "50")
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(modifier = cardModifier, title = "catcher count", value = "50")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(16.dp)
        ) {
            Text(text = "Last Codes")
            LazyColumn(
            ) {
                items(13) {
                    ListItem(
                        modifier = Modifier.padding(vertical = 8.dp),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        headlineContent = {
                            Text(text = "322")

                        },
                        supportingContent = {
                            Text(text = "google")
                        })
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    CodeCatcherTheme {
        Dashboard(DashboardViewModel())
    }
}