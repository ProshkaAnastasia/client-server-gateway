package service.action

import communication.Message
import service.server.AbstractServer

class DispatchAll(val message: Message) : LocalServerAction() {
    override fun execute(server: AbstractServer) {
        server.notifyEveryone(message)
    }
}