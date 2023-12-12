package com.example.spotifygroups.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.uistatemodel.HomeUiState
import com.example.spotifygroups.uistatemodel.QueueToJoin
import com.example.spotifygroups.viewmodel.AppViewModel
import com.example.spotifygroups.viewmodel.HomeViewModel

@Composable
fun HomeView(appViewModel: AppViewModel, queueRepository: QueueRepository) {
    var showSessionDialog by remember {
        mutableStateOf(false)
    }
    Column {
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Spotify Groups", fontWeight = FontWeight.Bold, fontSize = 32.sp
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.3f)
        )
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f),
        ) {
            Row(
                Modifier.fillMaxSize(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LargeFloatingActionButton({
                    showSessionDialog = true
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.PlayArrow, "Start Group Session", Modifier.size(64.dp))
                }
                LargeFloatingActionButton({
                    appViewModel.renderFriendView()
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.Person, "Friends", Modifier.size(64.dp))
                }
            }
        }
        if (showSessionDialog) {
            StartSessionDialog(appViewModel, queueRepository) { showSessionDialog = false }
        }
    }
}

@Composable
fun StartSessionDialog(
    appViewModel: AppViewModel,
    queueRepository: QueueRepository,
    onDismissRequest: () -> Unit
) {
    val homeViewModel = HomeViewModel(queueRepository)
    val homeUiState by homeViewModel.uiState.collectAsState()
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Start Session",
                    Modifier.padding(10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.75f)
                        .padding(10.dp)
                        .background(Color.LightGray)
                ) {
                    itemsIndexed(homeUiState.queuesToJoin) {_, queue ->
                        Row(
                            Modifier.fillMaxWidth(1f).background(Color.Cyan),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Creator: ${queue.creatorName}", color = Color.DarkGray, fontSize = 16.sp)
                            FloatingActionButton(onClick = {
                                queueRepository.setQueueId(queue.queueId, true)
                                appViewModel.renderSessionView()
                            }) {
                                Icon(Icons.Rounded.ArrowForward, "Join Queue", Modifier.size(48.dp), tint = Color.Cyan)
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxSize(1f), Arrangement.Center, Alignment.CenterVertically) {
                    LargeFloatingActionButton(onClick = onDismissRequest) {
                        Icon(Icons.Rounded.Close, "Go Back", Modifier.size(64.dp))
                    }
                    LargeFloatingActionButton(onClick = {
                        appViewModel.renderSessionView()
                    }) {
                        Icon(Icons.Rounded.PlayArrow, "Start Group Session", Modifier.size(64.dp))
                    }
                }
            }
        }
    }
}