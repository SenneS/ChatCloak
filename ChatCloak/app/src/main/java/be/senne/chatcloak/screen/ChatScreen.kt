package be.senne.chatcloak.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.viewmodel.ChatVM

@Composable
fun createChatScreen(nav : NavController, key_container : KeyContainer, ip : String, is_host : Boolean, vm : ChatVM = viewModel()) {
    Text(text = "I'm the host? ${is_host} and I'm going to connect to ${ip}")
    vm.myKey = key_container.myKeys
    vm.theirKey = key_container.theirKey
    vm.isHost = is_host

    vm.generateSessionKey()
    vm.setupConnection()

}