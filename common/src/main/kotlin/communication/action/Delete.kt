package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import types.SpaceMarine

@Serializable
@SerialName("delete")
class Delete(val element: SpaceMarine) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleDelete(this)
        }
        TODO()
    }
}