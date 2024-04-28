package ru.alex3koval.service.vo

/**
 * Тема сообщения
 */
enum class Topic(val value: String) {
    DOMAIN("domain");

    companion object {
        operator fun invoke(value: String): Result<Topic> = runCatching {
            val cleanValue = value.trim()

            Topic
                .entries
                .firstOrNull { it.value.equals(cleanValue, ignoreCase = true) }
                ?: throw RuntimeException("Топик '$value' не найден")
        }
    }
}