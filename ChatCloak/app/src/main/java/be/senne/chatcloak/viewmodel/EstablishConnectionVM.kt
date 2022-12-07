package be.senne.chatcloak.viewmodel

import androidx.lifecycle.ViewModel
import java.security.KeyPair

class EstablishConnectionVM : ViewModel() {

    lateinit var key : KeyPair
    lateinit var publicKey : String
    lateinit var ip : String

}