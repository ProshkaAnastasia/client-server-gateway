package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import user.User

@Serializable
@SerialName("execution")
class Execution(val token: String, val command: String, val arguments: ArrayList<@Polymorphic Any>) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleExecution(this, key)
        }
        TODO()
    }
    constructor(token: String, command: String, vararg arguments: Any) : this(token, command, ArrayList(arguments.toMutableList()))
}