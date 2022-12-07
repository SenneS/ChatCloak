package be.senne.chatcloak.viewmodel

import androidx.lifecycle.ViewModel
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement

class ChatVM : ViewModel() {
    lateinit var myKey : KeyPair
    lateinit var theirKey : String
    lateinit var friendIp : String
    lateinit var connectionSocket : Socket
    lateinit var sessionKey : ByteArray
    lateinit var sessionIv : ByteArray

    var isHost : Boolean = false

    lateinit var dataInputStream : DataInputStream
    lateinit var dataOutputStream : DataOutputStream

    fun setupConnection() {
        if(isHost) {
            val serverSocket = ServerSocket(21576)
            connectionSocket = serverSocket.accept()

            dataInputStream = DataInputStream(connectionSocket.getInputStream())
            dataOutputStream = DataOutputStream(connectionSocket.getOutputStream())
        }
        else {
            connectionSocket = Socket(friendIp, 21576)
            dataInputStream = DataInputStream(connectionSocket.getInputStream())
            dataOutputStream = DataOutputStream(connectionSocket.getOutputStream())
        }
    }

    fun generateSessionKey() {
        val keyAgreement = KeyAgreement.getInstance("ECDH", "BC")
        keyAgreement.init(myKey.private)

        val theirPublicKeyBytes = theirKey.encodeToByteArray()
        val keyFactory = KeyFactory.getInstance("ECDH", "BC")
        val theirPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(theirPublicKeyBytes))

        keyAgreement.doPhase(theirPublicKey, true)

        val sharedSecret = keyAgreement.generateSecret()
        sessionKey = sharedSecret.copyOfRange(0, 16)
        sessionIv = sharedSecret.copyOfRange(16, 32)
    }

}