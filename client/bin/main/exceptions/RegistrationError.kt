package exceptions

class RegistrationError : RuntimeException() {
    override val message: String = "Ошибка регистрации..."
}