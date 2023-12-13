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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
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
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.viewmodel.SessionInfoViewModel

@Composable
fun SessionInfoView(queueRepository: QueueRepository, onDismiss: () -> Unit) {
    val sessionInfoViewModel = SessionInfoViewModel(queueRepository)
    val sessionInfoUiState by sessionInfoViewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.2f)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Participants", fontWeight = FontWeight.Bold, fontSize = 32.sp)
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
                itemsIndexed(sessionInfoUiState) { _, item ->
                    Row(
                        Modifier.background(Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            if (item.name !== null) item.name else "No Name",
                            Modifier
                                .padding(5.dp, 0.dp)
                                .fillMaxWidth(0.8f)
                                .horizontalScroll(rememberScrollState()),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )
                        SmallFloatingActionButton(onClick = {
                            sessionInfoViewModel.removeParticipant(item) {
                                if (it) {
                                    sessionInfoViewModel.getParticipantNames()
                                }
                            }
                        }) {
                            Icon(Icons.Rounded.Close, "Remove friend", Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}