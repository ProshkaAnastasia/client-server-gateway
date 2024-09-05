package communication.action

import handler.AbstractClientHandler
import handler.AbstractHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import types.SpaceMarine

@Serializable
@SerialName("update")
class Update(val last: SpaceMarine, val new: SpaceMarine) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        TODO()
    }
}