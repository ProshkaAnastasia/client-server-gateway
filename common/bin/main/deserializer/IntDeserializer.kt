package deserializer

class IntDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return obj.toInt()
    }
}