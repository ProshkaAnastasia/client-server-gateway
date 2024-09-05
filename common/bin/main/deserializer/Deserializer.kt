package deserializer

interface Deserializer {
    fun decodeFromString(obj: String) : Any
}