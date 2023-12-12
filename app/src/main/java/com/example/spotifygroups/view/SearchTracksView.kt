package com.example.spotifygroups.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.viewmodel.SearchTracksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTracksDialog(
    searchTracksViewModel: SearchTracksViewModel = viewModel(),
    onDismissRequest: () -> Unit
) {
    val searchTracksUiState by searchTracksViewModel.uiState.collectAsState()
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Search Songs",
                    Modifier.padding(10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
            LazyColumn(
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.75f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(searchTracksUiState.searchResults) { index, item ->
                    Row(
                        Modifier
                            .background(Color.Black)
                            .fillMaxWidth(1f)
                            .padding(horizontal = 0.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        SearchItem(item, searchTracksViewModel)
                        if (index < searchTracksUiState.searchResults.count() - 1) {
                            Divider(Modifier, 2.dp, Color.Gray)
                        }
                    }
                }
            }
            OutlinedTextField(
                searchTracksUiState.query,
                onValueChange = {
                    searchTracksViewModel.updateQuery(it)
                },
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.4f),
                label = {
                    Text("Enter Prompt")
                }
            )
            Row(
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(onClick = {
                    onDismissRequest()
                }) {
                    Icon(Icons.Rounded.Close, "Close", Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
fun SearchItem(
    item: QPlayable,
    searchTracksViewModel: SearchTracksViewModel = viewModel(),
) {
    val artists = item.artists.joinToString { it }
    SmallFloatingActionButton(onClick = {
        searchTracksViewModel.removeFromSearchResult(item)
    }) {
        Icon(Icons.Rounded.Close, "Remove from result", Modifier.size(36.dp))
    }
    if (item.image !== null) {
        AsyncImage(
            item.image.url,
            contentDescription = item.name,
            Modifier.padding(5.dp, 0.dp)
        )
    }
    Text(
        "${item.name} - $artists",
        Modifier
            .padding(5.dp, 0.dp)
            .fillMaxWidth(0.8f)
            .horizontalScroll(rememberScrollState()),
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
    SmallFloatingActionButton(onClick = {
        searchTracksViewModel.addToQueue(item)
    }) {
        Icon(Icons.Rounded.Add, "Add to queue", Modifier.size(36.dp))
    }
}