import kotlinx.serialization.Serializable
import user.Status

@Serializable
data class AbstractCommand(val name: String, val realName: String, val description: String, val status: HashSet<Status>,
                           val primitiveTypes: ArrayList<String>, val referenceTypes: ArrayList<String>) {
    override fun toString(): String {
        return "$name - $description Примитивные типы - $primitiveTypes. Ссылочные типы - $referenceTypes."
    }
}