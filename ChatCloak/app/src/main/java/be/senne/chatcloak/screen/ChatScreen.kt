package be.senne.chatcloak.screen

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.viewmodel.ChatVM
import be.senne.chatcloak.viewmodel.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun createChatScreen(nav : NavController, key_container : KeyContainer, ip : String, is_host : Boolean, vm : ChatVM = viewModel()) {
    vm.key_container = key_container
    vm.friendIp = ip
    vm.isHost = is_host

    val statusText = remember {
        mutableStateOf("Attempting to connect to $ip")
    }
    val isSendingEnabled = remember { mutableStateOf(false) }


    var messages = vm.messages

    remember {
        vm.generateSessionKey()
        vm.setupConnection(onSuccess = {
            statusText.value = "Successfully connected :)"
            vm.startListener()
            isSendingEnabled.value = true
        }, onFailure = {
            statusText.value = "Failed to connected :("
        })
        0
    }

    var textFieldState = remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(statusText.value)
        })
    }, content = {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(messages) { index, item ->
                    MessageItem(msg = item)
                }
            }
            Divider(thickness = 2.dp)
            Row(Modifier.weight(0.1f)) {
                TextField(value = textFieldState.value, onValueChange = {
                    textFieldState.value = it
                })

                Button(onClick = {
                    vm.sendMessage(textFieldState.value)
                    textFieldState.value = ""
                }) {
                    Text("Send")
                }
            }
        }

    })

}

@Composable
fun MessageItem(msg : Message) {
    val alignment = if(msg.mine) Alignment.CenterEnd else Alignment.CenterStart
    val background = if(msg.mine) Color.Green else Color.LightGray

    Row() {
        if(msg.mine) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(modifier = Modifier
            .padding(4.dp)
            .background(background)
            .padding(5.dp)
            .requiredWidth(100.dp), horizontalAlignment = Alignment.Start) {
            Text(text = msg.content, textAlign = TextAlign.Center, color = Color.Black)
        }
        if(!msg.mine) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}