package be.senne.chatcloak

import android.content.res.Resources
import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.senne.chatcloak.compose.navigation
import be.senne.chatcloak.screen.createExchangeKeysScreen
import be.senne.chatcloak.ui.theme.ChatCloakTheme
import be.senne.chatcloak.viewmodel.Message
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var resources : Resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainActivity.resources = resources

        //vervang default bouncy castle met de echte.
        Security.removeProvider("BC");
        Security.addProvider(BouncyCastleProvider())

        super.onCreate(savedInstanceState)
        setContent {
            ChatCloakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navigation()
                }
            }
        }
    }
}


/*
@Preview
@Composable
fun ChatList() {
    val msg1 = Message("A", true)
    val msg2 = Message("B", true)
    val msg3 = Message("C", false)
    val msg4 = Message("D", false)
    val msg5 = Message("E", true)

    val _items = listOf(msg1, msg2, msg3, msg4, msg5)

    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(_items) { index, item ->
            MessageItem(msg = item)
        }
    }
}
*/