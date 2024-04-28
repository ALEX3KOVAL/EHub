package ru.alex3koval.service.vo

/**
 * Обертка для обработчика событий
 *
 * @property serializer Сериализатор payload события
 * @property handler Обработчик события
 */
data class EventHandler(
    val serializer: Any,
    val handler: Any
)