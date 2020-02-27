package com.hridin.exotesto.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DrmInfo(val licenseUrl: String, val token: String, val drmSystem: DrmSystem) : Parcelable