package deserializer.request

import communication.action.Authorization
import deserializer.Deserializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthorizationRequestDeserializer : Deserializer {
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString<Authorization>(obj)
    }
}