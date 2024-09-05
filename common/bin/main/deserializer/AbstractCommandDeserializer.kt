package deserializer

import AbstractCommand
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AbstractCommandDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString<AbstractCommand>(obj)
    }
}