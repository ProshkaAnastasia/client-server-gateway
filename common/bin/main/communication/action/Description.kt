package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import user.Status
import user.User

@Serializable
@SerialName("description")
class Description(val status: Status) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleDescription(this)
        }
        TODO("Not yet implemented")
    }
}