package be.senne.chatcloak.compose

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.KeyPairType
import be.senne.chatcloak.screen.createEstablishConnectionScreen
import be.senne.chatcloak.screen.createExchangeKeysScreen
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import java.security.KeyPair

@Composable
fun navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "exchange_keys_screen") {
        composable(route = "exchange_keys_screen") {
            BackHandler() {

            }
            
            createExchangeKeysScreen(navController)
        }

        composable(route = "establish_connection_screen/{key_container}", arguments = listOf(
            navArgument("key_container") {
                type = KeyPairType()
                nullable = false
            }
        )) {
            
            BackHandler(enabled = true) {
                navController.navigate("exchange_keys_screen")
            }

            val key_container = it.arguments?.getParcelable<KeyContainer>("key_container")
            if(key_container != null) {
                createEstablishConnectionScreen(navController, key_container)
            }
        }

        composable("chat_screen", arguments = listOf(
            navArgument("key_container") {
                type = KeyPairType()
                nullable = false
            },
            navArgument("is_host") {
                type = NavType.BoolType
                nullable = false
            }
        )) {
            BackHandler(enabled = true) {
                navController.navigate("exchange_keys_screen")
            }
        }


    }
}