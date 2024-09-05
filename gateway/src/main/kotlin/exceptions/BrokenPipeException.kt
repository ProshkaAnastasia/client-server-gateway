package exceptions

class BrokenPipeException(key: String) : RuntimeException("Соединение по адресу $key потеряно.")