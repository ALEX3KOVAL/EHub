package ru.alex3koval.service.extension

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Сериализатор для даты и времени
 */
object DateTimeAsStringSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return decoder.decodeString()
            .let {
                OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime()
            }
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        return value.atOffset(ZoneOffset.ofHours(3)).format(DateTimeFormatter.ISO_DATE_TIME)
            .let { encoder.encodeString(it) }
    }
}