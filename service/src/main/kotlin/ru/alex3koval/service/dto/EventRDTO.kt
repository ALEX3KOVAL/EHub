package ru.alex3koval.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.alex3koval.service.extension.DateTimeAsStringSerializer
import ru.alex3koval.service.vo.Topic
import java.time.LocalDateTime

@Serializable
data class EventRDTO(
    val id: Int,
    @SerialName("aggregate_id")
    val aggregateID: String,
    val hash: String,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("data")
    val json: String,
    @SerialName("producer")
    val producerName: String,
    @SerialName("type")
    val name: String,
    @Serializable(with = DateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = DateTimeAsStringSerializer::class)
    val updatedAt: LocalDateTime,
    val comment: String? = null,
    val topic: Topic? = null
)