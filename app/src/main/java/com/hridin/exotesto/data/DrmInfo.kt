package com.hridin.exotesto.data

import kotlinx.serialization.Serializable

@Serializable
data class DrmInfo(val licenseUrl: String, val token: String, val drmSystem: DrmSystem)