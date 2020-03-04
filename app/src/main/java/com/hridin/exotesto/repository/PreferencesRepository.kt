package com.hridin.exotesto.repository

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager
import com.hridin.exotesto.data.Stream
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list

class PreferencesRepository(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val KEY_STREAM_JSON = "stream_json"
    }

    var streamJson: List<Stream>
        get() = Json(JsonConfiguration.Stable)
            .parse(Stream.serializer().list, preferences.getString(KEY_STREAM_JSON, "[]")!!)
        set(value) {
            preferences.edit()
                .putString(KEY_STREAM_JSON, Json(JsonConfiguration.Stable).stringify(Stream.serializer().list, value))
                .apply()
        }


    fun setOfflineLicenseKeySetId(psshKey: String, license: ByteArray?) {
        if (license != null)
            preferences.edit()
                .putString(psshKey, Base64.encodeToString(license, Base64.DEFAULT))
                .apply()
    }

    fun getOfflineLicenseKeySetId(psshKey: String): ByteArray? {
        val licenseBase64 = preferences.getString(psshKey, null) ?: return null

        return Base64.decode(licenseBase64, Base64.DEFAULT)
    }

    fun removeOfflineLicenseKeySetId(psshKey: String) {
        preferences.edit().remove(psshKey).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}