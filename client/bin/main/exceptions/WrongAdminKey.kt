package exceptions

class WrongAdminKey: RuntimeException() {
    override val message: String = "Неверный ключ подключения. Авторизация прервана..."
}