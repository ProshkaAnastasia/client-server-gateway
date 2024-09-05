package exceptions

class IllegalCommandException : RuntimeException() {
    override val message: String = "Команда не найдена"
}