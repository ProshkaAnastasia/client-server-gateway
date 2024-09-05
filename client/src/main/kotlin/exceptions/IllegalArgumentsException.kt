package exceptions

class IllegalArgumentsException : RuntimeException() {
    override val message: String = "Недопустимые значения аргументов команды..."
}