package deserializer

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import types.SpaceMarine

class SpaceMarineDeserializer : Deserializer{
    override fun decodeFromString(obj: String): Any {
        return Json.decodeFromString(obj) as SpaceMarine
    }
}