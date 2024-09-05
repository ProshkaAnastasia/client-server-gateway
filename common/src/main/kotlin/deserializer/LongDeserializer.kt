package deserializer

class LongDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return obj.toLong()
    }

}