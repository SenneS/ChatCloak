package be.senne.chatcloak

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import be.senne.chatcloak.screen.createExchangeKeysScreen
import be.senne.chatcloak.ui.theme.ChatCloakTheme
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
                    createExchangeKeysScreen()
                }
            }
        }
    }
}