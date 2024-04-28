package ru.alex3koval.service

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import ru.alex3koval.service.vo.EventHandler

/**
 * Слушатель события
 */
interface EventListener {
    /**
     * Зарегистрировать слушатель события
     *
     * @param name Наименование события
     * @param serializer Сериализатор payload события
     * @param handler Обработчик события
     */
    fun <T> listen(name: String, serializer: KSerializer<T>, handler: (T) -> Result<Boolean>)

    /**
     * Получить обработчиков события
     *
     * @param eventName Наименование события
     */
    fun getHandlersForEvent(eventName: String): List<EventHandler>
}

/**
 * Зарегистрировать слушатель события
 *
 * @param handler Обработчик события
 */
@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> EventListener.listen(noinline handler: (T) -> Result<Boolean>) { // расширение для того, чтобы получить тип во время выполнения
    listen(
        name = T::class.qualifiedName!!,
        serializer = T::class.serializer(),
        handler = handler
    )
}
