package ru.alex3koval.service.vo

import kotlinx.serialization.Serializable

/**
 * Обертка для события
 *
 * @property name Наименование события
 * @property json JSON-строка payload события
 * @property topic Тема сообщения
 */
@Serializable
data class EventTransportContainer(val name: String, val json: String, val topic: Topic)