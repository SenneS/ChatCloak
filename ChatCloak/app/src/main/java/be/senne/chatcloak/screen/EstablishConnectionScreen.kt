package be.senne.chatcloak.screen

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.compose.navigation
import be.senne.chatcloak.viewmodel.EstablishConnectionVM
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

//accetabele limitaties voor mij.
private fun checkifValidIp(ip : String) : Boolean {
    val split = ip.split(".")
    if(split.size != 4) { return false }

    for(chunk in split) {
        val alsNummer = chunk.toIntOrNull()
        if(alsNummer == null || alsNummer < 0 || alsNummer > 255) { return false }
    }
    return true
}

@Composable
fun createEstablishConnectionScreen(nav: NavController, key_container : KeyContainer, vm : EstablishConnectionVM = viewModel()) {

    vm.key_container = key_container

    val openAlert =  remember { mutableStateOf(false) }
    val nextEnabled = remember { mutableStateOf(false) }
    
    if(openAlert.value) {
        AlertDialog(
            title = {Text("Connection Type")},
            text = { Text(text = "Select whether you want to be the host of the conversation or the client (whatever you pick should be the opposite of what your friend picked/picks")},
            onDismissRequest = {
                               openAlert.value = false
            },
            confirmButton = {
                            Button(onClick = {
                                val kc = vm.key_container
                                val ip = vm.ip
                                val is_host = true
                                nav.navigate("chat_screen/$kc/$ip/$is_host")
                            }) {
                                Text(text = "Host")
                            }
        }, dismissButton = {
                            Button(onClick = {
                                val kc = vm.key_container
                                val ip = vm.ip
                                val is_host = false
                                nav.navigate("chat_screen/$kc/$ip/$is_host")
                            }) {
                                Text(text = "Client")
                            }
        })
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Establish Connection")
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                                 openAlert.value = true
                }, enabled = nextEnabled.value) {
                    Text("Next")
                }
            }
        })

    }, content = { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            var text by remember { mutableStateOf(TextFieldValue("")) }
            TextField(
                value = text,
                onValueChange = { tfv ->
                    text = tfv
                    if(checkifValidIp(tfv.text)) {
                        nextEnabled.value = true
                        vm.ip = tfv.text
                    }else {
                        nextEnabled.value = false
                    }
                },
                label = { Text(text = "Your friend's IP address") },
                placeholder = { Text(text = "192.168.1.12") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(5.dp))

            val context: Context = LocalContext.current

            val manager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val ip: String = InetAddress.getByAddress(
                ByteBuffer
                    .allocate(Integer.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(manager.connectionInfo.ipAddress)
                    .array()
            ).hostAddress as String

            var myIpText by remember { mutableStateOf(TextFieldValue(ip)) }
            TextField(
                value = myIpText,
                enabled = false,
                onValueChange = {},
                label = { Text(text = "Your IP address: ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )



        }
    })
}