package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import user.User

@Serializable
@SerialName("registration")
class Registration (val user: User) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleRegistration(this)
        }
        TODO("Not yet implemented")
    }

}