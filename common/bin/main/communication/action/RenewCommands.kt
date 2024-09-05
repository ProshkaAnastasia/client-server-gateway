package communication.action

import AbstractCommand
import handler.AbstractClientHandler
import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("renew_commands")
class RenewCommands(val commands: HashMap<String, AbstractCommand>) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractClientHandler){
            return handler.handleRenewCommands(this)
        }
        TODO()
    }
}