package com.hridin.exotesto.repository

import android.content.res.AssetManager
import com.hridin.exotesto.data.Stream
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list

class StreamRepository(private val preferencesRepository: PreferencesRepository,
                       private val assetManager: AssetManager) {

    fun getStreamList(): List<Stream> {
        val streamJson = preferencesRepository.streamJson

        if (streamJson.isNotEmpty()) return streamJson

        val inputStream = assetManager.open("streams.json")
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()

        return Json(JsonConfiguration.Stable).parse(Stream.serializer().list, String(buffer))
    }

    fun setStreamList(streams: List<Stream>) {
        preferencesRepository.streamJson = streams
    }
}