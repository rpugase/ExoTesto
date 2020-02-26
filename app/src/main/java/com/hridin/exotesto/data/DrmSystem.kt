package com.hridin.exotesto.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.util.*

@Serializable(with = DrmSystemSerializer::class)
enum class DrmSystem {
    BUYDRM, IRDETTO
}

@Serializer(forClass = DrmSystem::class)
object DrmSystemSerializer {
    override val descriptor: SerialDescriptor
        get() = StringDescriptor

    override fun deserialize(decoder: Decoder): DrmSystem {
        return DrmSystem.valueOf(decoder.decodeString().toUpperCase(Locale.US))
    }

    override fun serialize(encoder: Encoder, obj: DrmSystem) {
        encoder.encodeString(obj.name.toLowerCase(Locale.US))
    }
}