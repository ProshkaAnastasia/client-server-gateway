package exceptions

class NoActiveServerException : RuntimeException("Нет доступных серверов для обработки сообщения") {
}