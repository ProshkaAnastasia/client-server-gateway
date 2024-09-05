package exceptions

class ArgumentsSerializationException: RuntimeException() {
    override val message: String = "Невалидные значения аргументов команды..."
}