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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifygroups.viewmodel.AppViewModel
import com.example.spotifygroups.viewmodel.HomeViewModel

@Composable
fun HomeView(appViewModel: AppViewModel) {
    val homeViewModel = HomeViewModel()
    val homeUiState by homeViewModel.uiState.collectAsState()
//    var showDialog by remember {
//        mutableStateOf(false)
//    }
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
//                    homeViewModel.update(HomeActions.GROUP)
//                    showDialog = true
                    appViewModel.renderSessionView()
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.PlayArrow, "Start Group Session", Modifier.size(64.dp))
                }
                LargeFloatingActionButton({
//                    homeViewModel.update(HomeActions.FRIENDS)
                }, shape = CircleShape) {
                    Icon(Icons.Rounded.Person, "Friends", Modifier.size(64.dp))
                }
            }
        }
//        if (showDialog) {
//            StartSessionDialog(appViewModel, homeViewModel) { showDialog = false }
//        }
    }
}

@Composable
fun StartSessionDialog(
    appViewModel: AppViewModel,
    homeViewModel: HomeViewModel = viewModel(),
    onDismissRequest: () -> Unit
) {
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
                Spacer(
                    Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.75f)
                        .padding(10.dp)
                        .background(Color.LightGray)
                )
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