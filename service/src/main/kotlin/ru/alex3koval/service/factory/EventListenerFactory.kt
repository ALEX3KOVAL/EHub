package ru.alex3koval.service.factory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.pulsar.client.api.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import ru.alex3koval.service.EventListener
import ru.alex3koval.service.persister.EventPersister
import ru.alex3koval.service.vo.EventHandler
import ru.alex3koval.service.vo.EventStatus
import ru.alex3koval.service.vo.EventTransportContainer
import ru.alex3koval.service.vo.Topic
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Фабрика слушателя события
 *
 * @property pulsarClient Клиент пульсара
 * @property logger Логгер
 * @property eventPersister Персистер события
 */
class EventListenerFactory(
    private val pulsarClient: PulsarClient,
    private val logger: Logger,
    private val eventPersister: EventPersister
) {
    val mapConsumer = mutableMapOf<Topic, Consumer<String>>()

    private val handlers: MutableMap<String, MutableList<EventHandler>> = mutableMapOf()

    val scope = CoroutineScope(Dispatchers.IO)

    private fun Message<String>.process(): Result<Message<String>> = runCatching {
        val eventTC = Json.decodeFromString<EventTransportContainer>(this.value)

        handlers[eventTC.name]?.forEach { eh ->
            val event = Json.decodeFromString(eh.serializer as DeserializationStrategy<Any>, eventTC.json)
            val handler = eh.handler as (Any) -> Result<Boolean>

            handler.invoke(event)
        }

        this
    }

    /**
     * Добавить получателя сообщения
     *
     * @param topic Тема сообщения
     * @param consumerName Наименование получателя
     * @param subscription Наименование подписки
     */
    private fun addToMap(topic: Topic, consumerName: String, subscription: String) {
        mapConsumer[topic] = pulsarClient
            .newConsumer(Schema.STRING)
            .topic(topic.value)
            .subscriptionName(subscription)
            .consumerName(consumerName)
            .negativeAckRedeliveryDelay(20, TimeUnit.SECONDS)
            .subscriptionType(SubscriptionType.Key_Shared)
            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
            .subscribe()
    }


    /**
     * Создать слушателя события
     *
     * @param topic Тема сообщения
     * @param consumerName Наименование получателя
     * @param subscription Наименование подписки
     */
    fun create(topic: Topic, consumerName: String, subscription: String): EventListener {
        if (!mapConsumer.containsKey(topic)) {
            addToMap(topic, consumerName, subscription)
        }

        return object : EventListener {
            override fun <T> listen(
                name: String,
                serializer: KSerializer<T>,
                handler: (T) -> Result<Boolean>
            ) {
                handlers[name]?.add(EventHandler(serializer, handler)) ?: handlers.put(
                    name,
                    mutableListOf(EventHandler(serializer, handler))
                )

                if (!handlers.containsKey(name)) {
                    runConsumer()
                }
            }

             private fun runConsumer(): Result<Unit> = runCatching {
                mapConsumer.forEach { (topicName, _) ->
                    scope.launch {
                        while (true) {
                            if (!mapConsumer.containsKey(topicName)) {
                                throw Exception("Не найден topic: $topicName")
                            }

                            val consumer = mapConsumer[topicName]!!
                            val msgR = runCatching { consumer.receive(2, TimeUnit.SECONDS) }

                            if (msgR.isFailure) {
                                logger.error(msgR.exceptionOrNull().toString())
                                continue
                            }

                            val msg = msgR.getOrThrow() ?: continue
                            val hash = msg.getProperty("hash")

                            eventPersister
                                .updateStatus(
                                    hash = hash,
                                    status = EventStatus.IN_PROCESS,
                                    updatedAt = LocalDateTime.now()
                                )
                                .getOrThrow()

                            try {
                                transaction {
                                    msg.process()
                                        .onSuccess {
                                            eventPersister
                                                .updateStatus(
                                                    hash = hash,
                                                    status = EventStatus.PROCESSED,
                                                    updatedAt = LocalDateTime.now()
                                                )
                                                .getOrThrow()

                                            consumer.acknowledge(msg)

                                            commit()
                                        }
                                        .onFailure { err ->
                                            consumer.negativeAcknowledge(msg)
                                            rollback()
                                            eventPersister.updateStatus(hash, EventStatus.ERROR, LocalDateTime.now()).getOrThrow()
                                            logger.error("Ошибка при обработке $msg", err)
                                        }
                                }
                            } catch (err: Exception) {
                                logger.error("Ошибка при обработке $msg", err)
                            }
                        }
                    }
                }
            }

            /**
             * Получить обработчиков события
             *
             * @param eventName Наименование события
             */
            override fun getHandlersForEvent(eventName: String): List<EventHandler> {
                return handlers[eventName] ?: emptyList()
            }
        }
    }
}