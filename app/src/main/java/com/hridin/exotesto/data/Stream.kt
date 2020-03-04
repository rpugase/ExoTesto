package com.hridin.exotesto.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Stream(val manifestUrl: String, val channelName: String, val drmInfo: DrmInfo? = null) : Parcelable