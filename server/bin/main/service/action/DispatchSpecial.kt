package service.action

import communication.Message
import service.server.AbstractServer

class DispatchSpecial(val message: Message) : LocalServerAction() {

    override fun execute(server: AbstractServer) {
        TODO("Not yet implemented")
    }
}