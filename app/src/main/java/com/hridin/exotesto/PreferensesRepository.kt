package com.hridin.exotesto

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager

class PreferensesRepository(context: Context) {

    companion object {
        private const val KEY_LICENSE = "license"
    }

    private val preferenses = PreferenceManager.getDefaultSharedPreferences(context)

    var offlineLicenseKeySetId: ByteArray?
        get() {
            val licenseBase64 = preferenses.getString(KEY_LICENSE, null) ?: return null

            return Base64.decode(licenseBase64, Base64.DEFAULT)
        }
        set(value) {
            preferenses.edit()
                .putString(KEY_LICENSE, Base64.encodeToString(value, Base64.DEFAULT))
                .apply()
        }
}