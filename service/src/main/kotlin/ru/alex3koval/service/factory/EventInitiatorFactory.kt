package ru.alex3koval.service.factory

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import ru.alex3koval.service.Event
import ru.alex3koval.service.EventInitiator
import ru.alex3koval.service.persister.EventPersister
import ru.alex3koval.service.vo.Topic
import java.util.*

/**
 * Фабрика инициатора события
 *
 * @property eventPersister Персистер события
 */
class EventInitiatorFactory(
    val eventPersister: EventPersister
) {
    /**
     * Создать инициатора события
     *
     * @param topic Тема сообщения
     */
    fun create(topic: Topic): EventInitiator = object : EventInitiator {
        override fun <T : Any> initiate(
            eventName: String,
            serializer: KSerializer<T>,
            data: Event<T>
        ): Result<Boolean> {
            val json = Json.encodeToJsonElement(serializer as SerializationStrategy<Any>, data.payload)

            // Если не передан id пользователя значит действие вызвал робот
            val userId = data.userId ?: 100500

            // Создаем уникальный хэш
            val hash = UUID.randomUUID().toString()

            // Добавляем событие в БД
            return eventPersister.add(
                aggregateId = data.key,
                hash = hash,
                userId = userId,
                json = json,
                producerName = data.producer,
                name = eventName,
                createdAt = data.createdAt,
                updatedAt = data.createdAt,
                comment = data.description,
                topic = topic
            )
        }
    }
}