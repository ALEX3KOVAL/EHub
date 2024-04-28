package ru.alex3koval.service

import kotlinx.serialization.Serializable
import ru.alex3koval.service.extension.DateTimeAsStringSerializer
import ru.alex3koval.service.vo.EventProducer
import java.time.LocalDateTime

/**
 * Событие
 *
 * @property createdAt Дата и время инициации события
 * @property userId Сотрудник, инициировавший событие
 * @property payload Данные события
 * @property producer Источник события
 * @property key Ключ события, события с одинаковым ключом будут выстраиваться в очередь на выполнение одним потребителем
 * @property description Описание события
 */
@Serializable
data class Event<T>(
    @Serializable(with = DateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    val payload: T,
    val producer: EventProducer,
    val key: String,
    val userId: Int? = null,
    val description: String? = null
)