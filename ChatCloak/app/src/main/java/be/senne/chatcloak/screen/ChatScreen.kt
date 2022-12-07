package be.senne.chatcloak.screen

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.viewmodel.ChatVM


@Composable
fun createChatScreen(nav : NavController, key_container : KeyContainer, ip : String, is_host : Boolean, vm : ChatVM = viewModel()) {
    vm.key_container = key_container
    vm.friendIp = ip
    vm.isHost = is_host

    remember {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        vm.generateSessionKey()
        vm.setupConnection()
        0
    }

    Column() {
        Text(text = "I'm the host? ${is_host} and I'm going to connect to ${ip}")
        var textFieldState = remember { mutableStateOf("")}
        TextField(value = textFieldState.value, onValueChange = {
            textFieldState.value = it
        })
        Button(onClick = {
            vm.sendMessage(textFieldState.value)
            textFieldState.value = ""
        }) {
            Text("Send Message")
        }
    }
}