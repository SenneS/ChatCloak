package be.senne.chatcloak.viewmodel

import androidx.lifecycle.ViewModel
import be.senne.chatcloak.KeyContainer
import org.bouncycastle.jce.interfaces.ECPublicKey
import java.io.DataInputStream
import java.io.DataOutputStream
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

    fun sendMessage(str : String) {
        val encryptedMessage = cbcEncrypt(str.encodeToByteArray(), sessionKey, sessionIv)
        println("sending packet with size ${encryptedMessage.size}")
        dataOutputStream.writeInt(encryptedMessage.size)
        dataOutputStream.write(encryptedMessage)
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