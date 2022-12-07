package be.senne.chatcloak.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.senne.chatcloak.KeyContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.interfaces.ECPublicKey
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ChatVM : ViewModel() {
    lateinit var key_container : KeyContainer
    lateinit var friendIp : String
    lateinit var connectionSocket : Socket
    lateinit var sessionKey : ByteArray
    lateinit var sessionIv : ByteArray

    lateinit var networkMutex : Mutex
    lateinit var networkJob : Job

    var isHost : Boolean = false

    private val _messagesState = mutableStateListOf<Message>()
    val messages: List<Message> = _messagesState

    lateinit var dataInputStream : DataInputStream
    lateinit var dataOutputStream : DataOutputStream

    fun setupConnection(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isHost) {
                    val serverSocket = ServerSocket(21576)
                    connectionSocket = serverSocket.accept()

                    dataInputStream = DataInputStream(connectionSocket.getInputStream())
                    dataOutputStream = DataOutputStream(connectionSocket.getOutputStream())
                } else {
                    connectionSocket = Socket()
                    connectionSocket.connect(InetSocketAddress(friendIp, 21576), 10000)
                    dataInputStream = DataInputStream(connectionSocket.getInputStream())
                    dataOutputStream = DataOutputStream(connectionSocket.getOutputStream())
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                }


            }
            catch(_ : Exception) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }

    fun startListener() {
        networkJob = viewModelScope.launch(Dispatchers.IO) {
           while(true) {
               val packetLength = dataInputStream.readInt()
               val encryptedMsg = ByteArray(packetLength)
               dataInputStream.read(encryptedMsg, 0, packetLength)
               val decryptedMsg = cbcDecrypt(encryptedMsg, sessionKey, sessionIv)

               _messagesState.add(Message(decryptedMsg.decodeToString(), false))

           }
        }
    }


    fun sendMessage(str : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val encryptedMessage = cbcEncrypt(str.encodeToByteArray(), sessionKey, sessionIv)
            println("sending packet with size ${encryptedMessage.size}")
            dataOutputStream.writeInt(encryptedMessage.size)
            dataOutputStream.write(encryptedMessage)

            _messagesState.add(Message(str, true))
        }
    }

    fun generateSessionKey() {


        val keyAgreement = KeyAgreement.getInstance("ECDH", "BC")

        val keyFactory = KeyFactory.getInstance("ECDH", "BC")

        println("@@@@@@@ PRIVATE KEY LENGTH: ${key_container.private.size}")

        val myPrivateKey =keyFactory.generatePrivate(PKCS8EncodedKeySpec(key_container.private))
        val theirPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(key_container.theirPublicKey))

        keyAgreement.init(myPrivateKey)
        keyAgreement.doPhase(theirPublicKey, true)

        val sharedSecret = keyAgreement.generateSecret()
        sessionKey = sharedSecret.copyOfRange(0, 16)
        sessionIv = sharedSecret.copyOfRange(16, 32)

        var printStr = "SessionKey: "
        for (byte in sessionKey) {
            printStr += String.format("%02X", byte)
        }
        println(printStr)

        printStr = "SessionIv: "
        for (byte in sessionIv) {
            printStr += String.format("%02X", byte)
        }
        println(printStr)


    }

    private fun createKey(key: ByteArray) : SecretKey {
        return SecretKeySpec(key, "AES")
    }
    private fun createIv(iv: ByteArray) : IvParameterSpec {
        return IvParameterSpec(iv)
    }
    fun cbcEncrypt(input: ByteArray, key: ByteArray, iv: ByteArray) : ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, createKey(key), createIv(iv))
        return cipher.doFinal(input)
    }

    fun cbcDecrypt(input: ByteArray, key: ByteArray, iv: ByteArray) : ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, createKey(key), createIv(iv))
        return cipher.doFinal(input)
    }

}

data class Message(val content : String, val mine : Boolean)