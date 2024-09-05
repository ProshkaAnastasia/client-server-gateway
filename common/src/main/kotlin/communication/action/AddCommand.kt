package communication.action

import AbstractCommand
import handler.AbstractHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("add_command")
class AddCommand(val command: AbstractCommand) : AbstractAction(){
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        TODO("Not yet implemented")
    }

}