package deserializer.request

import communication.action.Registration
import deserializer.Deserializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RegistrationRequestDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString<Registration>(obj)
    }
}