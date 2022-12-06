package be.senne.chatcloak.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.senne.chatcloak.viewmodel.EstablishConnectionVM
import be.senne.chatcloak.viewmodel.ExchangeKeysVM

@Composable
fun createExchangeKeysScreen(vm : EstablishConnectionVM = viewModel()) {
    val nextEnabled = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Establish Connection")
                Spacer(Modifier.weight(1f))
                Button(onClick = { /*TODO*/ }, enabled = nextEnabled.value) {
                    Text("Next")
                }
            }
        })

    }, content = { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

        }
    })
}