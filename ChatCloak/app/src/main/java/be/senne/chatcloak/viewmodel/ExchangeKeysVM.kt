package be.senne.chatcloak.viewmodel

import android.util.TypedValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import be.senne.chatcloak.MainActivity
import net.glxn.qrgen.android.QRCode
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.util.*

class ExchangeKeysVM() : ViewModel() {

    lateinit var key : KeyPair

    var publicKeyHasBeenObtained = false
    var publicKey = ""

    init {
        refreshKeyPair()
    }

    fun refreshKeyPair() {
        val ecParameter = ECGenParameterSpec("P-521")
        val keygen = KeyPairGenerator.getInstance("ECDH", "BC")
        keygen.initialize(ecParameter, SecureRandom())
        key = keygen.genKeyPair()

        val encodedKey = key.private.encoded
    }

    fun generateQrCode() : ImageBitmap {
        val keyBytes = key.public.encoded
        val keyPrefixedBase64 = "CLOAK:${Base64.getEncoder().encodeToString(keyBytes)}"
        val qrCode = QRCode.from(keyPrefixedBase64)
        val px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 380f, MainActivity.resources.displayMetrics))
        var qrBitmap = qrCode.withSize(px, px).bitmap()
        return qrBitmap.asImageBitmap()
    }

}