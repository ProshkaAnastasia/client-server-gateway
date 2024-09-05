package exceptions

class IllegalStatusException : RuntimeException() {
    override val message: String = "Команда не может быть запущена из текущей учетной записи..."
}