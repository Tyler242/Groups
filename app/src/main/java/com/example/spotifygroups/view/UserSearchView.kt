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
import com.example.spotifygroups.data.FriendRepository
import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.UserModel
import com.example.spotifygroups.viewmodel.UserSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchView(friendRepository: FriendRepository, onDismissRequest: () -> Unit) {
    val userSearchViewModel = UserSearchViewModel(friendRepository)
    val userSearchUiState by userSearchViewModel.uiState.collectAsState()
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
                    "Search for Friends",
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
                itemsIndexed(userSearchUiState.searchResults) { index, item ->
                    Row(
                        Modifier
                            .background(Color.Black)
                            .fillMaxWidth(1f)
                            .padding(horizontal = 0.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        UserResult(item, userSearchViewModel)
                        if (index < userSearchUiState.searchResults.count() - 1) {
                            Divider(Modifier, 2.dp, Color.Gray)
                        }
                    }
                }
            }
            OutlinedTextField(
                userSearchUiState.query,
                onValueChange = {
                    userSearchViewModel.updateQuery(it)
                },
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.4f),
                label = {
                    Text("Enter Username")
                }
            )
            FloatingActionButton(onClick = onDismissRequest) {
                Icon(Icons.Rounded.Close, "Close", Modifier.size(48.dp))
            }
        }
    }
}

@Composable
fun UserResult(user: UserModel, userSearchViewModel: UserSearchViewModel = viewModel()) {
    Text(
        "${user.name}",
        Modifier
            .padding(5.dp, 0.dp)
            .fillMaxWidth(0.8f)
            .horizontalScroll(rememberScrollState()),
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
    SmallFloatingActionButton(onClick = {
        userSearchViewModel.addFriend(user)
    }) {
        Icon(Icons.Rounded.Add, "Add Friend", Modifier.size(36.dp))
    }
}