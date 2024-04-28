package ru.alex3koval.service

import kotlinx.serialization.KSerializer

/**
 * Инициатор события
 */
interface EventInitiator {
    /**
     * Инициировать событие
     *
     * @param eventName Наименование события
     * @param serializer Сериализатор payload события
     * @param data Payload события
     */
    fun <T : Any> initiate(eventName: String, serializer: KSerializer<T>, data: Event<T>): Result<Boolean>
}