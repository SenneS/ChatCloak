package be.senne.chatcloak.viewmodel

import androidx.lifecycle.ViewModel
import be.senne.chatcloak.KeyContainer

class EstablishConnectionVM : ViewModel() {

    lateinit var key_container : KeyContainer
    lateinit var ip : String

}