package com.hridin.exotesto

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager

class PreferencesRepository(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setOfflineLicenseKeySetId(dashManifest: String, license: ByteArray?) {
        if (license != null)
            preferences.edit()
                .putString(dashManifest, Base64.encodeToString(license, Base64.DEFAULT))
                .apply()
    }

    fun getOfflineLicenseKeySetId(dashManifest: String): ByteArray? {
        val licenseBase64 = preferences.getString(dashManifest, null) ?: return null

        return Base64.decode(licenseBase64, Base64.DEFAULT)
    }
}