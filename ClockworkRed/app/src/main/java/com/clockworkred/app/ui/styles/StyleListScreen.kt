package com.clockworkred.app.ui.styles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.clockworkred.app.styles.StyleListViewModel

@Composable
fun StyleListScreen(
    navController: NavHostController,
    viewModel: StyleListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.error != null -> Text(uiState.error!!, modifier = Modifier.align(Alignment.Center))
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.styles) { style ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable { navController.navigate("style/${'$'}{style.id}") }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(style.name)
                                Text(style.description)
                            }
                        }
                    }
                }
            }
        }
    }
}
