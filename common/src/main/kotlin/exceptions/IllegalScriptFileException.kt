package exceptions

class IllegalScriptFileException : RuntimeException() {
    override val message: String? = "Ошибка в скриптовом файле. Прерывание выполнения..."
}