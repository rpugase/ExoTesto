package com.hridin.exotesto.data

import kotlinx.serialization.Serializable

@Serializable
data class Stream(val manifestUrl: String, val channelName: String, val drmInfo: DrmInfo)