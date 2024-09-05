package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import user.Status
import user.User

@Serializable
@SerialName("authorization")
class Authorization(val user: User, val status: Status = Status.STANDARD, val adminKey: String? = null) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleAuthorization(this, key)
        }
        TODO()
    }

}