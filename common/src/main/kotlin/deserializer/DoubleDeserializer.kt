package deserializer

class DoubleDeserializer: Deserializer {
    override fun decodeFromString(obj: String): Any {
        return obj.toDouble()
    }
}