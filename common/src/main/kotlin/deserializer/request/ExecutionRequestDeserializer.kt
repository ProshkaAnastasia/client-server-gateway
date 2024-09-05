package deserializer.request

import communication.action.Execution
import deserializer.Deserializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ExecutionRequestDeserializer : Deserializer{
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString<Execution>(obj)
    }
}