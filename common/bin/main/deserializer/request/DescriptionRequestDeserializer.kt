package deserializer.request

import communication.action.Description
import deserializer.Deserializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DescriptionRequestDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString<Description>(obj)
    }
}