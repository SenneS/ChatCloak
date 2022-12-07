package be.senne.chatcloak

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import java.security.KeyPair

@Parcelize
data class KeyContainer(val myKeys : KeyPair, val theirKey : String) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }
}

class KeyPairType : NavType<KeyContainer>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): KeyContainer? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): KeyContainer {
        return Gson().fromJson(value, KeyContainer::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: KeyContainer) {
        bundle.putParcelable(key, value)
    }

}