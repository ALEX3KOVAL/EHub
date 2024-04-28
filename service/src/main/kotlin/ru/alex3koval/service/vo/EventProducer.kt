package ru.alex3koval.service.vo

/**
 * Продюсер события
 */
sealed interface EventProducer {
    val value: String

    enum class Example(override val value: String) : EventProducer {
        MODULE("example.module")
    }
}