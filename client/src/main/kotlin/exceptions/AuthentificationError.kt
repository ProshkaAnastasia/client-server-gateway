package exceptions

class AuthentificationError : RuntimeException() {
    override val message: String = "Ошибка авторизации..."
}