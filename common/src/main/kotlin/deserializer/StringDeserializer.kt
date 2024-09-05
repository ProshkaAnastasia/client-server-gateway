package deserializer

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StringDeserializer : Deserializer{
    override fun decodeFromString(obj: String): Any {
        return obj
    }
}