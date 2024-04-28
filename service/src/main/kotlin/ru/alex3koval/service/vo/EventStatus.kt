package ru.alex3koval.service.vo

/**
 * Статус события
 */
enum class EventStatus(val value: Int) {
    CREATED(0) {
        override fun toString(): String {
            return "Создано"
        }
    },
    IN_PROCESS(1) {
        override fun toString(): String {
            return "В обработке"
        }
    },
    PROCESSED(2) {
        override fun toString(): String {
            return "Обработано"
        }
    },
    ERROR(3) {
        override fun toString(): String {
            return "Ошибка"
        }
    },
    WAITING(4) {
        override fun toString(): String {
            return "Ожидает обработки"
        }
    };

    companion object {
        operator fun invoke(value: Int): Result<EventStatus> {
            return entries
                .firstOrNull { it.value == value }
                ?.let { Result.success(it) }
                ?: Result.failure(Exception("Некорректный код статуса: $value"))
        }
    }
}