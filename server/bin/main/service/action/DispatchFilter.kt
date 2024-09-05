package service.action

import communication.Message
import service.server.AbstractServer

class DispatchFilter(val message: Message) : LocalServerAction() {
    override fun execute(server: AbstractServer) {
        TODO("Not yet implemented")
    }
}