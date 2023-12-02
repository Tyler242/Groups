package com.example.spotifygroups.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.UserRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.viewmodel.SearchTracksViewModel
import com.example.spotifygroups.viewmodel.SessionViewModel
import com.example.spotifygroups.viewmodel.SharedQueueViewModel

@Composable
fun SessionView(spotifyRepository: SpotifyRepository, userRepository: UserRepository) {
    val sharedQueueResultModel = SharedQueueViewModel(spotifyRepository, userRepository)
    val sessionViewModel =
        SessionViewModel(spotifyRepository, userRepository, sharedQueueResultModel)
    val searchTracksViewModel =
        SearchTracksViewModel(spotifyRepository, userRepository, sharedQueueResultModel)

    val queueUiState by sharedQueueResultModel.liveQueue.collectAsState()
    val sessionUiState by sessionViewModel.uiState.collectAsState()
    var showAddDialog by remember {
        mutableStateOf(false)
    }

    Column {
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.2f)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Shared Queue", fontWeight = FontWeight.Bold, fontSize = 32.sp)
        }
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.75f)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.75f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(queueUiState) { index, item ->
                    QueueItem(item, index, sessionViewModel)
                }
            }
            FloatingActionButton(onClick = {
                showAddDialog = true
            }) {
                Icon(Icons.Rounded.Add, "Add more tracks", Modifier.size(48.dp))
            }
        }
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
        ) {
            Row(
                Modifier.fillMaxSize(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton({
                    Log.i("SessionView", "Info")
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.Info, "Session Info", Modifier.size(48.dp))
                }
                FloatingActionButton({
                    Log.i("SessionView", "Play/Pause")
                    sessionViewModel.updatePlayState()
                }, shape = CircleShape) {
                    Icon(
                        if (sessionUiState.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Close,
                        "Play/Pause",
                        Modifier.size(48.dp)
                    )
                }
                FloatingActionButton({
                    Log.i("SessionView", "Next")
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.ArrowForward, "Next", Modifier.size(48.dp))
                }
            }
        }
    }
    if (showAddDialog) {
        SearchTracksDialog(searchTracksViewModel) {
            showAddDialog = false
        }
    }
}

@Composable
fun QueueItem(playable: Playable, index: Int, sessionViewModel: SessionViewModel = viewModel()) {
    val modifier = if (index == 0) Modifier.background(Color.DarkGray)
    else Modifier.background(Color.LightGray)
    val color = if (index == 0) Color.LightGray else Color.DarkGray

    var image = playable.album!!.images.find { it.width < 100 }
    if (image == null) {
        val lastImgIndex = playable.album.images.count()
        image = playable.album.images[lastImgIndex - 1]
    }

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        SmallFloatingActionButton(onClick = {
            sessionViewModel.removeFromQueue(playable, index)
        }) {
            Icon(Icons.Rounded.Close, "Remove track from queue", Modifier.size(36.dp))
        }
        AsyncImage(
            model = image.url,
            contentDescription = playable.name,
            Modifier.padding(5.dp, 0.dp)
        )
        Text(
            playable.name,
            Modifier
                .padding(5.dp, 0.dp)
                .fillMaxWidth(0.8f)
                .horizontalScroll(rememberScrollState()),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = color
        )
        if (index > 0) {
            SmallFloatingActionButton(onClick = {
                sessionViewModel.updateQueue(playable, index - 1)
            }) {
                Icon(Icons.Rounded.KeyboardArrowUp, "Move item in queue", Modifier.size(36.dp))
            }
        }
    }
}