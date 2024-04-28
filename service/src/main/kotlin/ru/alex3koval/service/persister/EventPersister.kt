package ru.alex3koval.service.persister

import kotlinx.serialization.json.JsonElement
import ru.alex3koval.service.vo.EventProducer
import ru.alex3koval.service.vo.EventStatus
import ru.alex3koval.service.vo.Topic
import java.time.LocalDateTime

/**
 * Персистер события
 */
interface EventPersister {
    /**
     * Добавить событие
     *
     * @param aggregateId ID агрегата события
     * @param hash Хеш-сумма payload события
     * @param userId ID пользователя, инициировавшего событие
     * @param json JSON payload события
     * @param producerName Наименование продюсера события
     * @param name Наименование события
     * @param comment Комментарий к событию
     * @param topic Тема события
     * @param createdAt Дата и время создания
     * @param updatedAt Дата и время обновления
     */
    fun add(
        aggregateId: String,
        hash: String,
        userId: Int,
        json: JsonElement,
        producerName: EventProducer,
        name: String,
        comment: String?,
        topic: Topic,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ): Result<Boolean>

    /**
     * Обновить статус события
     *
     * @param hash Хеш-сумма payload события
     * @param status Новый статус события
     * @param updatedAt Дата и время обновления
     */
    fun updateStatus(
        hash: String,
        status: EventStatus,
        updatedAt: LocalDateTime
    ): Result<Boolean>
}